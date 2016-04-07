import threading

import logging
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
    #Lock the thread for a second
    lock = threading.Lock()
    with lock:

        # Increase total logins by one
        global GLOBAL_CURRENT_EVENT, GLOBAL_LOGINS, GLOBAL_SN, \
            GLOBAL_SN_1, GLOBAL_DN, GLOBAL_PREVIOUS_DN, GLOBAL_OUT_OF_CONTROL_AMOUNT

        # logging.debug(GLOBAL_SN,
        #               GLOBAL_SN_1, GLOBAL_DN, GLOBAL_PREVIOUS_DN,
        #               GLOBAL_OUT_OF_CONTROL_AMOUNT)
        GLOBAL_LOGINS += 1

        # Extract the login from the call data
        login = Login.parse_from_json(request.json)
        print "Login Received",login

        # add the login to the current event
        GLOBAL_CURRENT_EVENT.add_login(login)

        # See if the current event is full or not
        if GLOBAL_CURRENT_EVENT.is_threshold_reached():
            #TODO PRINT EVENT IN LOG
            logging.debug(str(GLOBAL_CURRENT_EVENT))
            #Add current event to list of events
            GLOBAL_EVENT_LIST.append(GLOBAL_CURRENT_EVENT)

            # Calculate gfi
            gfi = GLOBAL_CURRENT_EVENT.calculate_gfi()
            print "Calculated GFI", gfi
            # Calculate zn
            zn = GLOBAL_CURRENT_EVENT.calculate_zn(TUNING_MU, TUNING_K)
            print "Calculated ZN", zn
            # SAVE OLD SN
            GLOBAL_SN_1 = GLOBAL_SN
            print "Global sn 1",GLOBAL_SN_1
            # Calculate new sn
            GLOBAL_SN += zn
            print "Global sn", GLOBAL_SN
            # Save Previous detection
            GLOBAL_PREVIOUS_DN = GLOBAL_DN
            print "Global previous dn", GLOBAL_PREVIOUS_DN
            # Perform detection
            GLOBAL_DN = detection_function()
            print "Global dn", GLOBAL_DN
            # Set Event in control or not.  Has to be negated to reflect
            # in control = no detection and out of control = detection
            GLOBAL_CURRENT_EVENT.set_control(not GLOBAL_DN)

            if GLOBAL_DN is True:
                print "Global DN is true"
                GLOBAL_OUT_OF_CONTROL_AMOUNT += 1
                if GLOBAL_PREVIOUS_DN is False:
                    print "Attack initiated"
                    # Package Epoch
                    epoch = package_epoch()
                    # Process it
                    GLOBAL_CLASSIFIER.process(epoch)
                    #Reset event
                    reset_current_event()
                else:
                    print "Attack in progress"
                    reset_current_event()
                    if GLOBAL_OUT_OF_CONTROL_AMOUNT > TUNING_OOC_MEDIUM_THRESHOLD:
                        epoch = package_epoch()
                        GLOBAL_CLASSIFIER.process(epoch)

            else:
                print "Global dn is false"
                if GLOBAL_PREVIOUS_DN is True:
                    print"Reverting to control"
                    # Reset counts
                    reset()
                    # Package epoch
                    epoch = package_epoch()
                    # Process Epoch
                    GLOBAL_CLASSIFIER.process(epoch)
                    #Reset event
                    reset_current_event()
                else:
                    print "Already in control"
                    reset_current_event()
    return "OK"


def main():
    #Set up logging
    logging.basicConfig(filename='example.log', level=logging.DEBUG)
    print "Starting det server"

    # Set up initial event
    global GLOBAL_CLASSIFIER
    reset()
    reset_current_event()
    GLOBAL_CLASSIFIER = Classifier(TUNING_MU,TUNING_H,TUNING_K)
    app.run(port=8003)


def shutdown_server():
    func = request.environ.get('werkzeug.server.shutdown')
    if func is None:
        raise RuntimeError('Not running with the Werkzeug Server')
    func()

'''Main function'''
if __name__ == '__main__':
    main()