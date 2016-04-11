import unittest


class EventTestCase(unittest.TestCase):

    def setUp(self):
        # commander.start_client("localhost","dummy","dummylog",True)
        self.GLOBAL_IP = "localhost"
        # time.sleep(10)

    def test_login_detection(self):
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
        pass



