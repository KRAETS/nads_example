import json
import time
import unittest
import urllib2

import attacksim
import commander


class EventTestCase(unittest.TestCase):

    def setUp(self):
        commander.start_server(True)
        self.GLOBAL_IP = "localhost"
        time.sleep(10)


    def test_login_detection(self):

        try:
            attacksim.main(None)
            time.sleep(20)
            f = open("singletonresults.txt","r")
            assert "yes" in f.readlines()

        except Exception as e:
            print "Problem contacting server", e
            exit()

    def tearDown(self):
        req = urllib2.Request('http://'+self.GLOBAL_IP+':5000/shutdown')
        req.add_header('Content-Type', 'application/json')
        response = urllib2.urlopen(req, json.dumps("shutdown"))



