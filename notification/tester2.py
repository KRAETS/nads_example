import os
import subprocess
import time
import unittest
import requests
textparams = "{\"marie\":{\"phonenumber\":\"7872364281\",\"phoneprovider\":\"att\",\"email\":\"\",\"notifiablealgorithms\":[\"sshdetectionserver\"]},\"antoine\":{\"phonenumber\":\"7872171762\",\"phoneprovider\":\"\",\"email\":\"\",\"notifiablealgorithms\":[\"sshdetectionserver\"]}}"
emailparams = "{\"marie\":{\"phonenumber\":\"\",\"phoneprovider\":\"tmobile\",\"email\":\"sully.rodriguez17@gmail.com\",\"notifiablealgorithms\":[\"sshdetectionserver\"]},\"ytiu\":{\"phonenumber\":\"\",\"phoneprovider\":\"\",\"email\":\"rookyann@gmail.com\",\"notifiablealgorithms\":\"\"}}"
validalgs = "{\"List\":[\"sshdetectionserver\"]}"
password = "jhkynisdwmmkecja"
email = "d3ath696@gmail.com"
scriptdir = os.getcwd() + "/notification.py"
VERBOSE = False

class EventTestCase(unittest.TestCase):
    # ---------------------------------------------------- initializer
    def setUp(self):
        global p
        print "Setting up test"

        p = subprocess.Popen(['python', scriptdir, textparams, validalgs, email, password])

        if VERBOSE:
            print 'starting server'
        time.sleep(15)

    # ------------------------------------- text test
    def test_text_notification(self):
        global GLOBAL_PROCESS_LIST, p
        global VERBOSE
        print "\nTEST: Correct and erroneous email -----------"

        if VERBOSE:
            print 'sending message'

        try:
            senddata = 'sshdetectionserver**test_email_notification'
            r = requests.get("http://localhost:8000/" + senddata, timeout=8)
        except requests.ConnectionError as e:
            print 'http exception', e
        except requests.HTTPError as e:
            print 'http not connected', e
        except requests.ConnectTimeout as e:
            print 'http invalid URL', e
        else:
            assert 'ERROR: unhandled error'

        if VERBOSE:
            print 'killing process'
        return True

    # ------------------------------------------------------ Tear Down
    def tearDown(self):
        global p
        p.terminate()
        print "Finishing test"



