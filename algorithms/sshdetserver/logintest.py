import json
import time
import unittest
import urllib2
import login
import paramiko as paramiko

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
            localdata = login.Login("Failed","127.0.0.1","god","juan")

            req = urllib2.Request('http://'+self.GLOBAL_IP+':8003/addlogin')
            req.add_header('Content-Type', 'application/json')
            response = urllib2.urlopen(req, json.dumps(localdata.__dict__))
            assert response.code == 200
        except Exception as e:
            print "Problem contacting server", e
            exit()

    def tearDown(self):
        req = urllib2.Request('http://'+self.GLOBAL_IP+':8003/shutdown')
        req.add_header('Content-Type', 'application/json')
        response = urllib2.urlopen(req, json.dumps("shutdown"))



