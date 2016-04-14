import unittest
import urllib2
import json
import commander

class EventTestCase(unittest.TestCase):

    def setUp(self):
        commander.start_client(True,"localhost","dummy","dummylog")
        self.GLOBAL_IP = "localhost"
        # time.sleep(10)

    def test_login_detection_node(self):
        print "Starting test"
        print "Opening file to write"
        file = open("dummy/dummylog",'w+')
        file.write("Failed, 201.201.201.201 ,Juan, 192.168.0.123 ")
        file.close()
        res = open("results.txt")
        found = False
        for line in res.readlines():
            if "yes" in line:
                found = True
                break
        assert found == True


    def tearDown(self):
        commander.stop()
        pass



