import subprocess, os
import sys
import getopt
import time

# --------------------------------------------------------- variables
emailparams = "{\"marie\":{\"phonenumber\":\"\",\"phoneprovider\":\"\",\"email\":\"rookyann@yahoo.com\",\"notifiablealgorithms\":\"12\"},\"ytiu\":{\"phonenumber\":\"\",\"phoneprovider\":\"\",\"email\":\"rookyann@gmail.com\",\"notifiablealgorithms\":\"\"}}"
textparams = "{\"marie\":{\"phonenumber\":\"7872171762\",\"phoneprovider\":\"att\",\"email\":\"\",\"notifiablealgorithms\":\"12\"},\"antoine\":{\"phonenumber\":\"7872171762\",\"phoneprovider\":\"sprint\",\"email\":\"\",\"notifiablealgorithms\":\"3\"}}"
configparams = "{}"
password = "rookyann@yahoo.com"
email = "password"
scriptdir = os.getcwd() + "/notification.py"
print '\n---------------------------------------------'
print '         STARTING NOTIFICATION TEST          '
print '---------------------------------------------\n'

# ----------------------------------------------------- test set up
def initialSetUp():
    global VERBOSE
    global RUNTRY
    print 'Starting initial configuration --------------'
    args = sys.argv[1:]
    count = 0
    print args
    print scriptdir

    if '-v' in args or '--verbose' in args:
        count += 1
        VERBOSE = True
        print 'verbose ON'
    if '-c' in args or '--config' in args:
        count += 1
        configSetUp()
    if '-e' in args or '--email' in args:
        count += 1
        emailTest()
    if '-t' in args or '--text' in args:
        count += 1
        textTest()

    if count < len(args):
        RUNTRY = False
        assert False, 'unhandled option'

    return True

# --------------------------------------------------- config set up
def configSetUp():
    global GLOBAL_PROCESS_LIST
    global VERBOSE
    if VERBOSE:
        print "\nTEST: Configuration set up ------------------"

    if VERBOSE:
        print 'creating notification process'
    p = subprocess.Popen(['python', scriptdir, configparams, password, email])
    GLOBAL_PROCESS_LIST.append(p)

    if VERBOSE:
        print 'sending message'
    time.sleep(3)
    try:
        h1 = http.client.HTTPConnection('localhost', 2000, timeout=10)
    except http.client.HTTPException:
        print 'http exception'
    except http/client.NotConnected:
        print 'http not connected'
    except http.client.InvalidURL:
        print 'http invalid URL'
    except http.client.UnknownProtocol:
        print 'http unknown protocol'
    else:
        assert 'ERROR: unhandled error'

    time.sleep(10)
    if VERBOSE:
        print 'killing process'
    p.terminate()
    return True

# ------------------------------------- correct and incomplete email
def emailTest():
    global GLOBAL_PROCESS_LIST
    global VERBOSE
    print "\nTEST: Correct and erroneous email -----------"

    if VERBOSE:
        print 'creating notification process'
    p = subprocess.Popen(['python', scriptdir, emailparams, password, email])
    GLOBAL_PROCESS_LIST.append(p)

    if VERBOSE:
        print 'sending message'
    time.sleep(3)
    try:
        h2 = http.client.HTTPConnection('localhost', 2000, timeout=10)
    except http.client.HTTPException:
        print 'http exception'
    except http/client.NotConnected:
        print 'http not connected'
    except http.client.InvalidURL:
        print 'http invalid URL'
    except http.client.UnknownProtocol:
        print 'http unknown protocol'
    else:
        assert 'ERROR: unhandled error'

    time.sleep(10)
    if VERBOSE:
        print 'killing process'
    p.terminate()
    return True

# -------------------------------------- correct and erroneous text
def textTest():
    global GLOBAL_PROCESS_LIST
    global VERBOSE
    print '\nTEST: Correct and erroneous text ------------'

    if VERBOSE:
        print 'creating notification process'
    p = subprocess.Popen(['python', scriptdir, textparams, password, email])
    GLOBAL_PROCESS_LIST.append(p)

    if VERBOSE:
        print 'sending message'
    time.sleep(3)
    try:
        h3 = http.client.HTTPConnection('localhost', 2000, timeout=10)
    except http.client.HTTPException:
        print 'http exception'
    except http/client.NotConnected:
        print 'http not connected'
    except http.client.InvalidURL:
        print 'http invalid URL'
    except http.client.UnknownProtocol:
        print 'http unknown protocol'
    else:
        assert 'ERROR: unhandled error'

    time.sleep(10)
    if VERBOSE:
        print 'killing process'
    p.terminate()
    return True

# ------------------------------------------------------------ main
if __name__ == '__main__':
    try:
        initialSetUp()
    except Exception as inst:
        print '\nERROR: Could not get arguments for initial set up'
        print '\n        UN-SUCCESSFUL TEST        '
        print '----------------------------------'
        sys.exit(0)

    print '\n        SUCCESSFUL TEST        '
    print '-------------------------------'
    sys.exit(0)