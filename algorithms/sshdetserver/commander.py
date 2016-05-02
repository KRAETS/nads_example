import json
import signal
import sys
import time
import logging
from multiprocessing import Process

import sshdetnode
import sshdetserver

GLOBAL_PROCESS_LIST = []
LOG_FILENAME = 'example.log'


def usage():
    """Legacy function to print out command line usage"""
    logging.debug( "Hi this is the most useful help ever")
    return


def signal_term_handler(a, b):
    """Function to handle sigterm and shutdown proceses"""
    logging.debug( "Protocol Detection Commander Module Successfully Killed")
    global GLOBAL_PROCESS_LIST
    for process in GLOBAL_PROCESS_LIST:
        process.terminate()
    sys.exit(0)


def stop():
    logging.debug( "Notification Module Successfully Killed")
    global GLOBAL_PROCESS_LIST
    for process in GLOBAL_PROCESS_LIST:
        process.terminate()


def main():
    """Starts a server, client or both depending on json parameters"""
    try:
        logging.debug( "The commander is starting!!!")
        logging.debug( sys.argv)
        logging.debug( sys.argv[1])
        # Get json
        workingstring = sys.argv[1]
        # workingstring = str(workingstring).replace('\\','')
        # jvar = '{"server":true,"folder":"../algorithms/sshdetserver/commander.py"}'
        # encoded  = json.dumps(str(sys.argv[1]))
        # jstring = str(sys.argv[1])
        parameter_map = json.loads(workingstring)
        logging.debug( "Passed the parsing!!")
        logging.debug( "Parameters:"+str( parameter_map))
        # See if verbose option is enabled
        verbose = False
        try:
            if parameter_map["verbose"] is True or parameter_map["verbose"] == "true":
                verbose = True
        except Exception as e:
            logging.debug( "No verbose option provided"+str(e))
        # Check if server option enabled and start a server
        supported_protocols = None
        protocol = None
        try:
            supported_protocols = json.loads(parameter_map["supported_protocols"])
            protocol = parameter_map["protocol"]
        except Exception as e:
            logging.debug( "Not enough parameters given"+str(e))
            exit(1)
        # logging.debug( supported_protocols, protocol
        try:
            if parameter_map["server"] == True or parameter_map["server"] == "true":
                customparams = None
                try:
                    if parameter_map["parameters"] is not None:
                        customparams = json.loads(parameter_map["parameters"])
                except Exception as e:
                    logging.debug("COuld not get custom parameters"+str(e))

                logging.debug( "Starting the server")
                start_server(verbose, supported_protocols, protocol, parameter_map["dataaddress"],parameter_map["serverport"],parameter_map["clientport"], customparams)
                logging.debug( "Waiting for initialization")
                time.sleep(10)
                logging.debug( "Continuing")
        except Exception as e:
            logging.debug( "Could not start server"+str(e))
        # Check if client option is enabled and start a monitoring client
        try:
            if parameter_map["client"] is True or parameter_map["client"] == "true":
                logging.debug( "Checking for server address...")
                logging.debug( "Address is"+str(parameter_map["serveraddress"]))
                server_address = parameter_map["serveraddress"]
                whitelist = None
                try:
                    whitelist = json.loads(parameter_map["whitelist"])
                except Exception as e:
                    logging.debug("No whitelist provided")
                start_client(verbose, server_address, parameter_map["monitoringfolder"],
                             parameter_map["monitoringfile"], supported_protocols, protocol,
                             parameter_map["dataaddress"], parameter_map["clientport"], whitelist)
        except Exception as e:
            logging.debug( "Could not start client"+str(e))
        # Wait for the processes to exit

        for process in GLOBAL_PROCESS_LIST:
            process.join()
        return

    except Exception as e:
        logging.debug( "Could not start the commander!!"+str(e))
        return 1

    # '''For legacy purposes'''

    # exit(0)
    #
    # try:
    #     opts, args = getopt.getopt(sys.argv[1:], "sc:hv")
    # except getopt.GetoptError as err:
    #     # print help information and exit:
    #     print str(err) # will print something like "option -a not recognized"
    #     # usage()
    #     sys.exit(2)
    #
    # server_address = None
    # verbose = False
    # for o, a in opts:
    #     if o == "-v":
    #         verbose = True
    #     elif o in ("-h", "--help"):
    #         usage()
    #         sys.exit()
    #     elif o in ("-s", "--server"):
    #         start_server(verbose)
    #     elif o in ("-c", "--client"):
    #         # Notify that client mode is enabled
    #         start_client(server_address, verbose)
    #
    #
    #     else:
    #         assert False, "unhandled option"
    # for process in GLOBAL_PROCESS_LIST:
    #     process.join()


def start_client(verbose, server_address, monitoringfolder, monitoringfile, supported_protocols, protocol, dataaddress, nodeport, whitelist):
    """Function to start a client process and monitor within a folder, a specific file"""
    global GLOBAL_PROCESS_LIST
    if verbose:
        logging.debug( "Client node")
    if verbose:
        logging.debug( "Server address"+str(server_address))
        logging.debug( "Starting up client script...")

    # Start up the client script separately
    p = Process(target=sshdetnode.main, args=(server_address, monitoringfolder,
                                              monitoringfile, supported_protocols, protocol,
                                              dataaddress, nodeport, whitelist))
    p.start()
    if verbose:
        logging.debug( "Client started!")

    GLOBAL_PROCESS_LIST.append(p)
    return


def start_server(verbose, supported_protocols, protocol, dataaddress, port, bcp, customparams):
    """Function to start a server process"""
    global GLOBAL_PROCESS_LIST
    if verbose:
        logging.debug( "Server node starting")
    kwargs = {'supportedprotocols':supported_protocols,'targetprotocol':protocol,
              'dataaddress':dataaddress, 'port':port, 'blockclientport':bcp}
    if customparams is not None:
        for param in customparams.keys():
            try:
                kwargs[param] = int(float(customparams[param]))
            except Exception as e:
                logging.debug("Could not parse custom param"+str(e))

    p = Process(target=sshdetserver.main, args=(verbose,), kwargs=kwargs)
    p.start()
    if verbose:
        logging.debug( "Server node started")
    GLOBAL_PROCESS_LIST.append(p)

if __name__ == '__main__':
    # Set the sigterm / sigint handlers
    signal.signal(signal.SIGTERM, signal_term_handler)
    signal.signal(signal.SIGINT, signal_term_handler)
    logging.basicConfig(filename=LOG_FILENAME, level=logging.DEBUG)
    # Start the main
    main()
