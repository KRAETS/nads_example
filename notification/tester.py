import httplib
import os
import subprocess
import time
import unittest

emailparams = "{\"marie\":{\"phonenumber\":\"\",\"phoneprovider\":\"\",\"email\":\"rookyann@yahoo.com\",\"notifiablealgorithms\":\"12\"},\"ytiu\":{\"phonenumber\":\"\",\"phoneprovider\":\"\",\"email\":\"rookyann@gmail.com\",\"notifiablealgorithms\":\"\"}}"
textparams = "{\"marie\":{\"phonenumber\":\"7872171762\",\"phoneprovider\":\"att\",\"email\":\"\",\"notifiablealgorithms\":\"12\"},\"antoine\":{\"phonenumber\":\"7872171762\",\"phoneprovider\":\"sprint\",\"email\":\"\",\"notifiablealgorithms\":\"3\"}}"
configparams = "{}"
password = "rookyann@yahoo.com"
email = "password"
scriptdir = os.getcwd() + "/notification.py"
VERBOSE = False

class EventTestCase(unittest.TestCase):
    # ---------------------------------------------------- initializer
    def setUp(self):
        print "Setting up test"

    # --------------------------------------------------- config set up
    def test_configuration_setup(self):
        global GLOBAL_PROCESS_LIST
        if VERBOSE:
            print "\nTEST: Configuration set up ------------------"

        if VERBOSE:
            print 'creating notification process'
        p = subprocess.Popen(['python', scriptdir, configparams, password, email])

        if VERBOSE:
            print 'sending message'
        time.sleep(3)
        try:
            h1 = httplib.HTTPConnection('localhost', 2000, timeout=10)
        except httplib.HTTPException:
            print 'http exception'
        else:
            assert 'ERROR: unhandled error'

        time.sleep(10)
        if VERBOSE:
            print 'killing process'
        p.terminate()
        return True

    # ------------------------------------- correct and incomplete email
    def test_email_notification(self):
        global GLOBAL_PROCESS_LIST
        global VERBOSE
        print "\nTEST: Correct and erroneous email -----------"

        if VERBOSE:
            print 'creating notification process'
        p = subprocess.Popen(['python', scriptdir, emailparams, password, email])

        if VERBOSE:
            print 'sending message'
        time.sleep(3)
        try:
            h2 = httplib.HTTPConnection('localhost', 2001, timeout=10)
        except httplib.HTTPException:
            print 'http exception'
        except http.client.NotConnected:
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
    def test_text_notification(self):
        global GLOBAL_PROCESS_LIST
        global VERBOSE
        print '\nTEST: Correct and erroneous text ------------'

        if VERBOSE:
            print 'creating notification process'
        p = subprocess.Popen(['python', scriptdir, textparams, password, email])

        if VERBOSE:
            print 'sending message'
        time.sleep(3)
        try:
            h3 = httplib.HTTPConnection('localhost', 2002, timeout=10)
        except httplib.HTTPException:
            print 'http exception'
        else:
            assert 'ERROR: unhandled error'

        time.sleep(10)
        if VERBOSE:
            print 'killing process'
        p.terminate()
        return True

    # ------------------------------------------------------ Tear Down
    def tearDown(self):
        print "Finishing test"



