import json
import sys
import signal
import time
from multiprocessing import Process

import sshdetnode
import sshdetserver

GLOBAL_PROCESS_LIST = []


def usage():
    print "Hi this is the most useful help ever"
    return

def signal_term_handler(a,b):
    print "Notification Module Successfully Killed"
    global GLOBAL_PROCESS_LIST
    for process in GLOBAL_PROCESS_LIST:
        process.terminate()
    sys.exit(0)

def stop():
    print "Notification Module Successfully Killed"
    global GLOBAL_PROCESS_LIST
    for process in GLOBAL_PROCESS_LIST:
        process.terminate()

def main():
    try:
        print "The commander is starting!!!"
        print sys.argv
        print sys.argv[1]
        workingstring = sys.argv[1]
        # workingstring = str(workingstring).replace('\\','')
        # jvar = '{"server":true,"folder":"../algorithms/sshdetserver/commander.py"}'
        # encoded  = json.dumps(str(sys.argv[1]))
        # jstring = str(sys.argv[1])
        parameter_map = json.loads(workingstring)
        print "Passed the parsing!!"
        print "Parameters:", parameter_map
        verbose = False
        try:
            if parameter_map["verbose"] is True or parameter_map["verbose"] == "true":
                verbose = True
        except Exception as e:
            print "No verbose option provided"

        try:
            if parameter_map["server"] is True or parameter_map["server"] == "true":
                print "Starting the server"
                start_server(verbose)
                print "Waitning for initialization"
                time.sleep(10)
                print "Continuing"
        except Exception as e:
            print e

        try:
            if parameter_map["client"] is True or parameter_map["client"] == "true":
                print "Checking for server address..."
                print "Address is",parameter_map["serveraddress"]
                server_address = parameter_map["serveraddress"]
                start_client(verbose,server_address,"/var/log/","auth.log")
        except Exception as e:
            print "Could not start client",e


        for process in GLOBAL_PROCESS_LIST:
            process.join()

        return
    except Exception as e:
        print "Could not start the commander!!", e
        return 1
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


def start_client(verbose, server_address, monitoringfolder, monitoringfile):
    global GLOBAL_PROCESS_LIST
    if verbose:
        print "Client node"
    if verbose:
        print "Server address", server_address
        print "Starting up client script..."

    # Start up the client script separately
    p = Process(target=sshdetnode.main, args=(server_address, monitoringfolder, monitoringfile))
    p.start()
    GLOBAL_PROCESS_LIST.append(p)
    return

def start_server(verbose):
    global GLOBAL_PROCESS_LIST
    if verbose:
        print "Server node"
    p = Process(target=sshdetserver.main, args=())
    p.start()
    GLOBAL_PROCESS_LIST.append(p)

if __name__ == '__main__':
    signal.signal(signal.SIGTERM, signal_term_handler)
    signal.signal(signal.SIGINT, signal_term_handler)

    main()
