import json
import time
import unittest
import urllib2

import commander
import sshdetnode


class EventTestCase(unittest.TestCase):

    def setUp(self):
        commander.start_server(True)
        self.GLOBAL_IP = "localhost"
        time.sleep(10)

    def test_login_detection(self):
        print "Starting test"
        try:
            localdata = sshdetnode.analyzeLogin()
            req = urllib2.Request('http://'+self.GLOBAL_IP+':5000/addlogin')
            req.add_header('Content-Type', 'application/json')
            response = urllib2.urlopen(req, json.dumps(localdata.__dict__))
            assert response.code == 200
        except Exception as e:
            print "Problem contacting server", e
            exit()

    def tearDown(self):
        req = urllib2.Request('http://'+self.GLOBAL_IP+':5000/shutdown')
        req.add_header('Content-Type', 'application/json')
        response = urllib2.urlopen(req, json.dumps("shutdown"))



