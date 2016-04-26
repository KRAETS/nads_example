#---------------------------------------------------------- import subprocess
from easysnmp import snmp_walk, EasySNMPError, EasySNMPConnectionError, EasySNMPTimeoutError
import re, signal
import time, json, sys
import os.path
import requests
import datetime
import urllib2


# ------------------------------------------------------------------ variables
ips = sys.argv[1]
print ips
ips = ((ips.replace("\"[","[")).replace("]\"","]")).replace("\\\"","\"")
args = json.loads(ips)
ipaddresses = args["ips"]
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
NOTIFICATION_SYSTEM_ADDRESS = "http://127.0.0.1:8000/"
ALGORITHM_NAME = "loop_detection"


def notify(message):
    #Notify of the event
    try:
        request = NOTIFICATION_SYSTEM_ADDRESS + ALGORITHM_NAME + "**" + urllib2.quote("Loop_Dectection" + message,safe='')
        req = urllib2.Request(request)
        response = urllib2.urlopen(req)
        print response
    except Exception as e:
        print "Could not send message:",e

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
        print i
        if oldfile:
            with open(logdir, "r+") as logfile:
                outData = json.loads(logfile.read())
            print "check for dup"
            print outData

            if outData.has_key(i):
                try:
                    hi = snmp_walk(outData[i], hostname=i, community="bigece1", version=2, timeout=1)
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
                            message_to_be_sent = "Loop Detected at switch with ip " + i + " at port " + port
                            notify(message_to_be_sent)
                            testdict[testcyc] = {i: port + " in file"}
                            date = '{:%b %d %H:%M:%S}'.format(datetime.datetime.now())
                            payload = "Anomaly_Name: loop, Date:" + date + ", IP:" + i + ", Port:" + port
                            r = requests.post("http://localhost:8002/senddata", data=payload)
                            print r.status_code


            else:
                 #hi = snmp_get(line, hostname=i, community="cappy-test", version=2)
                 noexists = True
                 print "else"

        if not(oldfile) or noexists:
            print "getting all info"
            try:
                hi = snmp_walk('iso', hostname=i, community="bigece1", version=2, timeout=1)
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
                            message_to_be_sent = "Loop Detected at switch with ip " + i + " at port " + port
                            notify(message_to_be_sent)
                            date = '{:%b %d %H:%M:%S}'.format(datetime.datetime.now())
                            payload = "Anomaly_Name: loop, Date:" + date + ", IP:" + i + ", Port:" + port
                            r = requests.post("http://localhost:8002/senddata", data=payload)
                            print r.status_code



        with open(logdir, "w") as logfile:
            json.dump(inData, logfile)
        with open(testlog, "w") as testfile:
            json.dump(testdict, testfile)
        oldfile = True

    testcyc += 1
    elapsed_time = time.time()
    time.sleep(sleeptime)



