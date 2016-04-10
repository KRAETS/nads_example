import os
import subprocess
import time
import unittest
import requests

emailparams = "{\"marie\":{\"phonenumber\":\"\",\"phoneprovider\":\"\",\"email\":\"rookyann@yahoo.com\",\"notifiablealgorithms\":\"12\"},\"ytiu\":{\"phonenumber\":\"\",\"phoneprovider\":\"\",\"email\":\"rookyann@gmail.com\",\"notifiablealgorithms\":\"\"}}"
textparams = "{\"marie\":{\"phonenumber\":\"7872171762\",\"phoneprovider\":\"att\",\"email\":\"\",\"notifiablealgorithms\":\"12\"},\"antoine\":{\"phonenumber\":\"7872171762\",\"phoneprovider\":\"sprint\",\"email\":\"\",\"notifiablealgorithms\":\"3\"}}"
configparams = "{}"
validalgs = "{List:[\"sshdetectionserver\"]}"
password = "email"
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
            senddata = 'sshdetectionserver**test_configuration_setup'
            r = requests.get("http://localhost:8000/", data=senddata, timeout=8)
        except requests.ConnectionError:
            print 'http exception'
        except requests.HTTPError:
            print 'http not connected'
        except requests.ConnectTimeout:
            print 'http invalid URL'
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
            senddata = 'sshdetectionserver**test_email_notification'
            r = requests.get("http://localhost:8000/", data=senddata, timeout=8)
        except requests.ConnectionError:
            print 'http exception'
        except requests.HTTPError:
            print 'http not connected'
        except requests.ConnectTimeout:
            print 'http invalid URL'
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
            senddata = 'sshdetectionserver**test_text_notification'
            r = requests.get("http://localhost:8000/", data=senddata, timeout=8)
        except requests.ConnectionError:
            print 'http exception'
        except requests.HTTPError:
            print 'http not connected'
        except requests.ConnectTimeout:
            print 'http invalid URL'
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



