import threading

import logging

import signal
from flask import Flask
from flask import request

from classifier import Classifier
from epoch import Epoch
from event import Event
from login import Login

app = Flask(__name__)

# Tuning parameters
TUNING_MU = 3
TUNING_K = 1
TUNING_H = 2
TUNING_AVERAGE_OOC_ARL = 3
TUNING_OOC_MEDIUM_THRESHOLD = 4
TUNING_EVENT_THRESHOLD = 10

# Global variables
GLOBAL_LOGINS = 0
GLOBAL_SN = 0
GLOBAL_SN_1 = 0
GLOBAL_DN = False
GLOBAL_PREVIOUS_DN = False
GLOBAL_EVENT_LIST = []
GLOBAL_OUT_OF_CONTROL_AMOUNT = 0
GLOBAL_CURRENT_EVENT = None
GLOBAL_CLASSIFIER = None

#Debug
debug = True


# Other parameters
supported_protocols = \
{
    "SSH":"sshd",
    "SMTP":"smtpd"
}
protocol = "SSH"

@app.route('/')
def hello_world():
    return 'Server Up!'


def detection_function():
    result = GLOBAL_SN > GLOBAL_SN_1 and GLOBAL_SN > TUNING_H
    return result


def reset():
    global GLOBAL_OUT_OF_CONTROL_AMOUNT, GLOBAL_SN, GLOBAL_SN_1
    GLOBAL_OUT_OF_CONTROL_AMOUNT = 0
    GLOBAL_SN = 0
    GLOBAL_SN_1 = 0
    return


def package_epoch():
    out_of_control_events = []
    # Get ooc events
    count = 0
    for i in reversed(GLOBAL_EVENT_LIST):
        #Get events that are out of control (true)
        if i.get_control() is True:
            if count > GLOBAL_OUT_OF_CONTROL_AMOUNT:
                break
            out_of_control_events.append(i)
            count += 1
    # Get events leading up to OOC
    history = []
    for i in range(0, GLOBAL_OUT_OF_CONTROL_AMOUNT+TUNING_AVERAGE_OOC_ARL):
        if len(GLOBAL_EVENT_LIST)-1-i < 0:
            break
        history.append(GLOBAL_EVENT_LIST[len(GLOBAL_EVENT_LIST)-1-i])
    # Create epoch
    epoch = Epoch(out_of_control_events, history)

    return epoch


def reset_current_event():
    global GLOBAL_CURRENT_EVENT
    GLOBAL_CURRENT_EVENT = Event(TUNING_EVENT_THRESHOLD)
    return

@app.route('/getcurrentepoch', methods=['GET'])
def get_epoch():
    global GLOBAL_CLASSIFIER
    return str(GLOBAL_CLASSIFIER.current_epoch)


@app.route('/shutdown', methods=['POST'])
def shutdown():
    shutdown_server()
    return 'Server shutting down...'


@app.route('/addlogin', methods=['POST'])
def add_login():
    global debug
    if debug:
        logging.debug( "Got a login!")
    # Lock the thread for processing
    if debug:
        logging.debug( "Acquiring lock")
    lock = threading.Lock()

    with lock:
        try:
            if debug:
                logging.debug( "Lock acquired")

            # Increase total logins by one
            global GLOBAL_CURRENT_EVENT, GLOBAL_LOGINS, GLOBAL_SN, \
                GLOBAL_SN_1, GLOBAL_DN, GLOBAL_PREVIOUS_DN, GLOBAL_OUT_OF_CONTROL_AMOUNT

            # logging.debug(GLOBAL_SN,
            #               GLOBAL_SN_1, GLOBAL_DN, GLOBAL_PREVIOUS_DN,
            #               GLOBAL_OUT_OF_CONTROL_AMOUNT)
            GLOBAL_LOGINS += 1

            # Extract the login from the call data
            login = Login.parse_from_json(request.json)
            if debug:
                logging.debug( "Login Received"+str(login.get_status())+ str(login.get_client()))


            # add the login to the current event
            GLOBAL_CURRENT_EVENT.add_login(login)

            # See if the current event is full or not
            if GLOBAL_CURRENT_EVENT.is_threshold_reached():
                # TODO logging.debug( EVENT IN LOG
                logging.debug(str(GLOBAL_CURRENT_EVENT))
                #Add current event to list of events
                GLOBAL_EVENT_LIST.append(GLOBAL_CURRENT_EVENT)

                # Calculate gfi
                gfi = GLOBAL_CURRENT_EVENT.calculate_gfi()
                if debug:
                    logging.debug( "Calculated GFI"+str(gfi))
                # Calculate zn
                zn = GLOBAL_CURRENT_EVENT.calculate_zn(TUNING_MU, TUNING_K)
                if debug:
                    logging.debug( "Calculated ZN"+str(zn))
                # SAVE OLD SN
                GLOBAL_SN_1 = GLOBAL_SN
                if debug:
                    logging.debug( "Global sn 1"+str(GLOBAL_SN_1))
                # Calculate new sn
                GLOBAL_SN += zn
                if debug:
                    logging.debug( "Global sn"+str(GLOBAL_SN))
                # Save Previous detection
                GLOBAL_PREVIOUS_DN = GLOBAL_DN
                if debug:
                    logging.debug( "Global previous dn"+str(GLOBAL_PREVIOUS_DN))
                # Perform detection
                GLOBAL_DN = detection_function()
                if debug:
                    logging.debug( "Global dn"+str(GLOBAL_DN))
                # Set Event in control or not.  Has to be negated to reflect
                # in control = no detection and out of control = detection
                GLOBAL_CURRENT_EVENT.set_control(not GLOBAL_DN)

                if GLOBAL_DN is True:
                    # logging.debug( "Global DN is true"
                    GLOBAL_OUT_OF_CONTROL_AMOUNT += 1
                    if GLOBAL_PREVIOUS_DN is False:
                        if debug:
                            logging.debug( "Attack initiated")
                        # Package Epoch
                        epoch = package_epoch()
                        # Process it
                        GLOBAL_CLASSIFIER.process(epoch)
                        #Reset event
                        reset_current_event()
                    else:
                        if debug:
                            logging.debug( "Attack in progress")
                        reset_current_event()
                        if GLOBAL_OUT_OF_CONTROL_AMOUNT % TUNING_OOC_MEDIUM_THRESHOLD == 0:
                            epoch = package_epoch()
                            GLOBAL_CLASSIFIER.process(epoch)

                else:
                    # logging.debug( "Global dn is false"
                    if GLOBAL_PREVIOUS_DN is True:
                        if debug:
                            logging.debug("Reverting to control")
                        # Reset counts
                        reset()
                        # Package epoch
                        epoch = package_epoch()
                        # Process Epoch
                        GLOBAL_CLASSIFIER.process(epoch)
                        #Reset event
                        reset_current_event()
                    else:
                        if debug:
                            logging.debug( "Already in control")
                        reset_current_event()
        except Exception as e:
            logging.debug( "Problem analyzing"+str(e))
    if debug:
        logging.debug( "Done analyzing, returning ok")
    return "OK"


def signal_term_handler(a, b):
    """Function to handle sigterm and shutdown proceses"""
    logging.debug( "PROTOCOL ATTACK DETECTION SERVER Successfully Killed")
    try:
        shutdown_server()
    except Exception as e:
        logging.debug( "Could not shut down gracefully"+str(e))
        exit(1)
    exit(0)


def main(debug_enabled, tuning_mu=3, tuning_k=1,
         tuning_h= 2,_tuning_average_ooc_arl=3,
         tuning_ooc_med_thresh=4, tuning_event_threshold=10,
         supportedprotocols=supported_protocols, targetprotocol=protocol):
    # Set shutdown hooks
    signal.signal(signal.SIGTERM, signal_term_handler)
    signal.signal(signal.SIGINT, signal_term_handler)

    try:
        # Initialize the parameters for the algorithm
        global debug, TUNING_MU, TUNING_K, TUNING_H, TUNING_AVERAGE_OOC_ARL, \
            TUNING_OOC_MEDIUM_THRESHOLD, TUNING_EVENT_THRESHOLD, supported_protocols, protocol, \
            GLOBAL_CLASSIFIER, supported_protocols, protocol
        # Give them values
        TUNING_MU = tuning_mu
        TUNING_K = tuning_k
        TUNING_H =tuning_h
        TUNING_AVERAGE_OOC_ARL =_tuning_average_ooc_arl
        TUNING_OOC_MEDIUM_THRESHOLD =  tuning_ooc_med_thresh
        TUNING_EVENT_THRESHOLD = tuning_event_threshold
        # Enable debug
        debug = debug_enabled

        # Protocol type
        supported_protocols = supportedprotocols
        protocol = targetprotocol

        if debug:
            pass
            # logging.debug( "Using parameters", debug, TUNING_MU, TUNING_K, TUNING_H, TUNING_AVERAGE_OOC_ARL,\
            #     TUNING_OOC_MEDIUM_THRESHOLD,TUNING_EVENT_THRESHOLD)

        #Set up logging
        if debug:
            logging.basicConfig(filename='example.log', level=logging.DEBUG)
            logging.debug( "Starting det server")

        # Set up initial event
        reset()
        reset_current_event()
        GLOBAL_CLASSIFIER = Classifier(TUNING_MU,TUNING_H,TUNING_K, supported_protocols, protocol)
        app.run(host='0.0.0.0',port=8003)
    except Exception as e:
        logging.debug( "Could not start server!!"+str(e))
        exit(1)


def shutdown_server():
    func = request.environ.get('werkzeug.server.shutdown')
    if func is None:
        raise RuntimeError('Not running with the Werkzeug Server')
    func()

'''Main function'''
if __name__ == '__main__':
    supported_protocols = \
        {
            "SSH": "sshd",
            "SMTP": "smtpd"
        }

    main(True, supportedprotocols=supported_protocols, targetprotocol="SSH")
