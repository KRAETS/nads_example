import getopt
import sys
from multiprocessing import Process

import sshdetnode
import sshdetserver

GLOBAL_PROCESS_LIST = []


def usage():
    print "Hi this is the most useful help ever"
    return


def main():
    try:
        opts, args = getopt.getopt(sys.argv[1:], "sc:hv")
    except getopt.GetoptError as err:
        # print help information and exit:
        print str(err) # will print something like "option -a not recognized"
        # usage()
        sys.exit(2)
    server_address = None
    verbose = False
    for o, a in opts:
        if o == "-v":
            verbose = True
        elif o in ("-h", "--help"):
            usage()
            sys.exit()
        elif o in ("-s", "--server"):
            start_server(verbose)
        elif o in ("-c", "--client"):
            # Notify that client mode is enabled
            start_client(server_address, verbose)


        else:
            assert False, "unhandled option"

    for process in GLOBAL_PROCESS_LIST:
        process.join()

def start_client(server_address, verbose):
    global GLOBAL_PROCESS_LIST
    if verbose:
        print "Client node"
    if verbose:
        print "Server address", server_address
        print "Starting up client script..."

    # Start up the client script separately
    p = Process(target=sshdetnode.main, args=(server_address,))
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
    main()
