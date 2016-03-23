import json
import time
import unittest
import urllib2

import requests

import attacksim
import commander


class EventTestCase(unittest.TestCase):

    def setUp(self):
        commander.start_server(True)
        self.GLOBAL_IP = "localhost"
        time.sleep(10)


    def test_login_detection(self):

        try:

            attacksim.main()
            time.sleep(30)
            stringthing = 'http://'+self.GLOBAL_IP+':5000/getcurrentepoch'
            r = requests.get(stringthing)

            assert r.status_code == 200

        except Exception as e:
            print "Problem contacting server", e
            exit()

    def tearDown(self):
        req = urllib2.Request('http://'+self.GLOBAL_IP+':5000/shutdown')
        req.add_header('Content-Type', 'application/json')
        response = urllib2.urlopen(req, json.dumps("shutdown"))



