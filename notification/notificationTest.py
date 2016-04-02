from multiprocessing import Process
import sys
import getopt
import time

# --------------------------------------------------------- variables
verbose = False
emailparams = {}
textparams = {}
configparams = {}

# ----------------------------------------------------- test set up
def initialSetUp():
    try:
        opts, args = getopt.getopt(sys.argv[1:], ['e', 'c', 't', 'k', 'v'], ['config', 'email', 'text', 'kill'])
    except getopt.GetoptError as err:
        print str(err)
        return False

    for o, a in opts:
        if o is '-v':
            verbose = True
        if o is ('-c' or '--config'):
            configSetUp()
        elif o is ('-e' or '--email'):
            emailTest()
        elif o is ('-t' or '--text'):
            textTest()
        elif o is ('-k' or '--kill'):
            killTest()
        else:
            assert False, 'unhandled option'

        if verbose:
            print "initial set up test"

    return True

# --------------------------------------------------- config set up
def configSetUp():
    if verbose:
        print "configure set up test"
    p = Process(target=sshdetnode.main, args=configparams)
    p.start()
    GLOBAL_PROCESS_LIST.append(p)
    time.sleep(5)
    killTest()
    return True

# ------------------------------------- correct and incomplete email
def emailTest():
    global GLOBAL_PROCESS_LIST
    if verbose:
        print "correct and erroneous email test"
    p = Process(target=sshdetnode.main, args=emailparams)
    p.start()
    GLOBAL_PROCESS_LIST.append(p)
    time.sleep(5)
    killTest()
    return True

# -------------------------------------- correct and erroneous text
def textTest():
    global GLOBAL_PROCESS_LIST
    if verbose:
        print 'correct and erroneous email test'
    p = Process(target=sshdetnode.main, args=textparams)
    p.start()
    GLOBAL_PROCESS_LIST.append(p)
    time.sleep(5)
    killTest()
    return True

# ------------------------------------------------------------ kill
def killTest():
    global GLOBAL_PROCESS_LIST
    if verbose:
        print 'kill test'
    for process in GLOBAL_PROCESS_LIST:
        process.terminate()
        process.join()
    return True

# ------------------------------------------------------------ main
if __name__ == '__main__':
    initialSetUp()
    if runtry:
        print 'ERROR: Could not get arguments for initial set up'
        sys.exit(0)
