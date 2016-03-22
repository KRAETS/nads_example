#---------------------------------------------------------- import subprocess
from easysnmp import snmp_walk, EasySNMPError, EasySNMPConnectionError, EasySNMPTimeoutError
import re, signal
import time, json, sys
import os.path

# ------------------------------------------------------------------ variables
ips = sys.argv[1]
ipaddresses = json.loads(ips)
# ipaddresses = ["136.145.59.152"]
testlog = os.getcwd() + "/testLog.json"
logdir = os.getcwd() + "/oidLog.json"

sleeptime = 6 * 5
oldfile = False
inData = dict()
noexists = False
start_time = time.time()
unavail_time = dict((x, 0) for x in ipaddresses)
testdict = dict()
testcyc = 0

#------------------------------------------------------- siganal termination
def signal_term_handler(signal, frame):
    print "killed"
    with open(logdir, "w") as logfile:
        json.dump(inData, logfile)
    sys.exit(0)

signal.signal(signal.SIGTERM, signal_term_handler)
signal.signal(signal.SIGINT, signal_term_handler)

#----------------------------------------------------------------- file check
if os.path.exists(logdir):
    print "file found"
    oldfile = True
else:
    print "file NOT found"


#------------------------------------------------------------------ detection
while True:
    for i in ipaddresses:
        if oldfile:
            with open(logdir, "r+") as logfile:
                outData = json.loads(logfile.read())
            print "check for dup"
            print outData

            if outData.has_key(i):
                try:
                    hi = snmp_walk(outData[i], hostname=i, community="cappy-test", version=2, timeout=1)
                except EasySNMPConnectionError:
                    if (unavail_time[i]- time.time() < 0) or ( unavail_time[i]- time.time() >= 21600):
                        #send notification to user that switch is down
                        if unavail_time[i]- time.time() < 0:
                            unavail_time[i] = time.time()
                    print "Error connecting to switch"
                    testdict[testcyc] = {i: "Error connecting to switch"}
                except EasySNMPTimeoutError:
                    if (unavail_time[i]- time.time() < 0) or ( unavail_time[i]- time.time() >= 21600):
                        #send notification to user that switch is down
                        if unavail_time[i]- time.time() < 0:
                            unavail_time[i] = time.time()
                    print "Error connecting to switch"
                    testdict[testcyc] = {i: "Error connecting to switch"}

                except EasySNMPError:
                    testdict[testcyc] = {i: "Error querying data"}
                else:
                    unavail_time[i] = 0
                    for line in hi:
                        oid=line.oid
                        snmp_type=line.snmp_type
                        value = line.value
                        if str(value) == "2":
                            print line
                            match = re.search(r"((.*.6.1.2.1.17.2.15.1.3).(\d+))", str(oid))
                            port = match.group(3)
                            oid_no_port = match.group(2)
                            inData[i] = oid_no_port
                            print port
                            testdict[testcyc] = {i: port + " in file"}
                            #print dude

            else:
                 #hi = snmp_get(line, hostname=i, community="cappy-test", version=2)
                 noexists = True
                 print "else"

        if not(oldfile) or noexists:
            print "getting all info"
            try:
                hi = snmp_walk('iso', hostname=i, community="cappy-test", version=2, timeout=1)
            except EasySNMPConnectionError:
                print "Error connecting to switch"
                testdict[testcyc] = {i: "Error connecting to switch"}
                if (unavail_time[i]- time.time() < 0) or ( unavail_time[i]- time.time() >= 21600):
                    #send notification to user that switch is down
                    if unavail_time[i]- time.time() < 0:
                        unavail_time[i] = time.time()
            except EasySNMPTimeoutError:
                if (unavail_time[i]- time.time() < 0) or ( unavail_time[i]- time.time() >= 21600):
                    #send notification to user that switch is down
                    if unavail_time[i]- time.time() < 0:
                        unavail_time[i] = time.time()
                print "Error connecting to switch"
                testdict[testcyc] = {i: "Error connecting to switch"}
            except EasySNMPError:
                print "Error querying data"
                testdict[testcyc] = {i: "Error querying data"}
            else:
                unavail_time[i] = 0
                dude = 0
                noexists = False
                for item in hi:
                    oid=item.oid
                    snmp_type=item.snmp_type
                    value = item.value
                    if ".6.1.2.1.17.2.15.1.3" in str(oid):
                        match = re.search(r"((.*.6.1.2.1.17.2.15.1.3).(\d+))", str(oid))
                        port = match.group(3)
                        oid_no_port = match.group(2)
                        inData[i] = oid_no_port
                        if str(value) == "2":
                            testdict[testcyc] = {i: port + " not in file"}
                            print port
                            #print dude


        with open(logdir, "w") as logfile:
            json.dump(inData, logfile)
        with open(testlog, "w") as testfile:
            json.dump(testdict, testfile)
        oldfile = True

    testcyc += 1
    elapsed_time = time.time()
    time.sleep(sleeptime)



