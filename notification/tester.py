import os
import subprocess
import time
import unittest
import requests

emailparams = "{\"marie\":{\"phonenumber\":\"\",\"phoneprovider\":\"tmobile\",\"email\":\"rookyann@gmail.com\",\"notifiablealgorithms\":[\"sshdetectionserver\"]},\"ytiu\":{\"phonenumber\":\"\",\"phoneprovider\":\"\",\"email\":\"rookyann@gmail.com\",\"notifiablealgorithms\":\"\"}}"
validalgs = "{\"List\":[\"sshdetectionserver\"]}"
password = "jhkynisdwmmkecja"
email = "d3ath696@gmail.com"
scriptdir = os.getcwd() + "/notification.py"
VERBOSE = True
p=None

class EventTestCase(unittest.TestCase):
    # ---------------------------------------------------- initializer
    def setUp(self):
        global p
        print "Setting up test"

        p = subprocess.Popen(['python', scriptdir, emailparams, validalgs, email, password])

        if VERBOSE:
            print 'starting server'
        time.sleep(15)

    # --------------------------------------------------- email test
    def test_email_setup(self):
        global GLOBAL_PROCESS_LIST, p
        if VERBOSE:
            print "\nTEST: Configuration set up ------------------"

        if VERBOSE:
            print 'Sending message'

        try:
            senddata = 'sshdetectionserver**test_configuration_setup'
            r = requests.get("http://localhost:8000/"+senddata, timeout=8)
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
