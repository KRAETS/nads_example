import unittest
import commander
import sshdetnode
import urllib2
import json

class EventTestCase(unittest.TestCase):

    def setUp(self):
        commander.start_server(True)
        self.GLOBAL_IP = "localhost"

    def test_login_detection(self):

        try:
            localdata = sshdetnode.analyzeLogin()
            req = urllib2.Request('http://'+self.GLOBAL_IP+':5000/addlogin')
            req.add_header('Content-Type', 'application/json')
            response = urllib2.urlopen(req, json.dumps(localdata.__dict__))
            assert response.code == 200
        except Exception as e:
            print "Problem contacting server", e
            exit()




