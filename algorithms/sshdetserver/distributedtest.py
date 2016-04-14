import json
import os
import time
import unittest
import urllib2

import attacksim
import commander


class DistributedTest(unittest.TestCase):

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
            found = False
            for line in f.readlines():
                if "yes" in line:
                    found = True
                    break
            assert found == True

        except Exception as e:
            print "Problem contacting server", e
            exit(0)

    def tearDown(self):
        """Close the server"""
        req = urllib2.Request('http://'+self.GLOBAL_IP+':8003/shutdown')
        req.add_header('Content-Type', 'application/json')
        response = urllib2.urlopen(req, json.dumps("shutdown"))



