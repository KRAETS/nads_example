import json
import os
import time
import unittest
import urllib2

import attacksim
import commander


class EventTestCase(unittest.TestCase):

    def setUp(self):
        """Set up the test by starting the server"""
        commander.start_server(True)
        self.GLOBAL_IP = "localhost"
        time.sleep(10)
        try:
            os.remove("distributedresults.txt")
        except Exception as e:
            print "File not found"

    def test_login_detection(self):
        """Start the attack with the simulator"""
        try:
            attacksim.main(True)
            time.sleep(10)
            f = open("distributedresults.txt","r")
            assert "yes" in f.readlines()

        except Exception as e:
            print "Problem contacting server", e
            exit()

    def tearDown(self):
        """Close the server"""
        req = urllib2.Request('http://'+self.GLOBAL_IP+':8003/shutdown')
        req.add_header('Content-Type', 'application/json')
        response = urllib2.urlopen(req, json.dumps("shutdown"))


