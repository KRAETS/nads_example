from multiprocessing import Process
import sys
import getopt
import time

# --------------------------------------------------------- variables
verbose = False

# ----------------------------------------------------- test set up
def initialSetUp():
    try:
        opts, agrs = getopt.getopt(sys.argv[1:], "sc:hv")
    except getopt.GetoptError as err:
        print str(err)
        return False

    for o, a in opts:
        if o is '-v':
            verbose = True
        elif o is ('-c' or '--config'):
            emailTest()
        elif o is ('-e' or '--email'):
            emailTest()
        elif o is ('-t' or '--text'):
            textTest()
        elif o is ('-k' or '--kill'):
            killTest()
        else:
            assert False, 'unhandled option'
    return True

# --------------------------------------------------- config set up
def configSetUp():

    time.sleep(5)
    killTest()
    return True

# ------------------------------------- correct and incomplete email
def emailTest():
    if verbose:
        print 'correct and erroneous email test'
    p = Process(target=sshdetnode.main, args=(server_address,monitoringserveraddress,monitoringfile))
    p.start()
    GLOBAL_PROCESS_LIST.append(p)
    time.sleep(5)
    killTest()
    return True

# -------------------------------------- correct and erroneous text
def textTest():
    if verbose:
        print 'correct and erroneous email test'
    p = Process(target=sshdetnode.main, args=(server_address,monitoringserveraddress,monitoringfile))
    p.start()
    GLOBAL_PROCESS_LIST.append(p)
    time.sleep(5)
    killTest()
    return True

# ------------------------------------------------------------ kill
def killTest():
    if verbose:
        print 'kill test'
    for process in GLOBAL_PROCESS_LIST:
        process.terminate()
        process.join()
    return True
        
# ------------------------------------------------------------ main
if __name__ == '__main__':
    global GLOBAL_PROCESS_LIST
    if False in initialSetUp():
        print 'ERROR: Could not get arguments for initial set up'
        sys.exit(0)
