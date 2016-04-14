import json
import time
import unittest
import urllib2
import os
import attacksim
import commander


class SingletonTest(unittest.TestCase):

    def setUp(self):
        commander.start_server(True)
        self.GLOBAL_IP = "localhost"
        time.sleep(10)
        try:
            os.remove("singletonresults.txt")
        except Exception as e:
            print "File not found"


    def test_singleton_detection(self):

        try:
            attacksim.main(None)
            time.sleep(10)
            f = open("singletonresults.txt","r")
            found = False
            for line in f.readlines():
                if "yes" in line:
                    found = True
                    break
            assert found == True

        except Exception as e:
            print "Problem contacting server", e
            exit()

    def tearDown(self):
        req = urllib2.Request('http://'+self.GLOBAL_IP+':8003/shutdown')
        req.add_header('Content-Type', 'application/json')
        response = urllib2.urlopen(req, json.dumps("shutdown"))



