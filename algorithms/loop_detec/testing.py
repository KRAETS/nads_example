import unittest, sys, subprocess, signal, time, json
import subprocess, os, time
from threading import Timer

class OutcomesTest(unittest.TestCase):

    def test_pass(self):
        testlog = os.getcwd() + "/testLog.json"
        logdir = os.getcwd() + "/oidLog.json"
        inData = dict()
        testdict = dict()
        with open(logdir, "w") as logfile:
            json.dump(inData, logfile)
        with open(testlog, "w") as testfile:
            json.dump(testdict, testfile)
        para = "{\"ips\":[\"136.145.59.152\"]}"
        testdir = os.getcwd() + "/Main_Script_Prototype.py"
        p = subprocess.Popen(['python', testdir, para])
        time.sleep(120)
        p.terminate()
        with open(testlog, "r") as logfile:
            logdata = json.loads(logfile.read())
        testagaisnt = {"0": {"136.145.59.152": "31 not in file"}, "1": {"136.145.59.152": "31 in file"}}
        logfile.close()
        self.assertDictEqual(testagaisnt, logdata)

    def test_no_connect(self):
        testlog = os.getcwd() + "/testLog.json"
        logdir = os.getcwd() + "/oidLog.json"
        inData = dict()
        testdict = dict()
        with open(logdir, "w") as logfile:
            json.dump(inData, logfile)
        with open(testlog, "w") as testfile:
            json.dump(testdict, testfile)
        para = "{\"ips\":[\"196.145.59.152\"]}"
        testdir = os.getcwd() + "/Main_Script_Prototype.py"
        p = subprocess.Popen(['python', testdir, para])
        time.sleep(40)
        p.terminate()
        p.kill()
        testdir = os.getcwd() + "/testLog.json"
        with open(testlog, "r") as logfile:
            logdata = json.loads(logfile.read())
        testagaisnt = {"0": {"196.145.59.152": "Error connecting to switch"}, "1": {"196.145.59.152": "Error connecting to switch"}}
        logfile.close()
        self.assertDictEqual(testagaisnt, logdata)


if __name__ == '__main__':
    unittest.main()


