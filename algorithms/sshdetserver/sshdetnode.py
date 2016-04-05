import json
import os
import time
import urllib
import urllib2
from datetime import datetime

from operator import attrgetter

from watchdog.events import FileSystemEventHandler
from watchdog.observers import Observer

from login import Login

GLOBAL_IP = None
MONITORING_FOLDER = "/var/log"
FILE_TO_MONITOR = "mylog"
KQL_SERVER = "localhost:9200/"
LAST_CHECK = None
def analyzeLogin():
    time.sleep(10)
    global LAST_CHECK
    #Query the data
    querystring = 'SELECT \ ALL*{protocol,portnumber,status,id,ip_address,datetime} \ from \ ALL/{protocol,portnumber,status,id,ip_address} \ where ( \ ALL*status \="Failed" or \ ALL*status \="Accepted" ) and \ ALL*datetime \ like "*Apr *"'
    query_url = 'http://localhost:9200/_kql?kql='
    completequery = query_url + urllib.quote(querystring, safe='')

    req = urllib2.Request(completequery)
    results = None
    try:
        response = urllib2.urlopen(req)
        printthings = response.read()
        results = json.loads(printthings)
        print results


    except Exception as e:
        print "Could not open kql server"
        return None

    #Sort by time
    reslist = results["hits"]["hits"]
    print 'y'
    usefulentries = []
    for item in reslist:
        entry = item["_source"]
        try:
            s = entry["Date"]
            usefulentries.append(entry)
        except Exception as e:
            print "Useless"
        print entry
    for entry in usefulentries:
        d = entry["Date"]
        entry["Date"] = datetime.strptime(entry["Date"], '%b %d %H:%M:%S')

        print entry["Date"]
    usefulentries.sort(key=lambda x: x["Date"], reverse=True)
    #Compare last check with first result
    if LAST_CHECK is None:
        LAST_CHECK = usefulentries[len(usefulentries)-1]
    data = None
    if LAST_CHECK == usefulentries[0]:
        # if firstresult is lessthan or equal to last check then return none
        return None
    else:
        data = Login(False, "", "", "")
        if usefulentries[0]["Status"][0] == "Failed":
            data.set_status(False)
        else:
            data.set_status(True)
        data.set_client(usefulentries[0]["ClientIp"][0])
        data.set_host("127.0.0.1")
        data.set_protocol("SSH")
        data.set_user(usefulentries[0]["UserName"][0])
        f = open("results.txt", "w")
        f.write("yes")
        f.close()
        LAST_CHECK = usefulentries[0]
        return data

class MyHandler(FileSystemEventHandler):

    def catch_all(self, event, op):
        print "Caught something", event
        if event.is_directory:
            return

        filename = event.src_path
        extension = os.path.splitext(filename)[-1].lower()
        if FILE_TO_MONITOR in filename and op is 'MOD':

            try:
                localdata = analyzeLogin()
                if localdata is None:
                    print "Old result"
                else:
                    req = urllib2.Request('http://'+GLOBAL_IP+'/addlogin')
                    req.add_header('Content-Type', 'application/json')
                    response = urllib2.urlopen(req, json.dumps(localdata.__dict__))
                    print response
            except Exception as e:
                print "Problem contacting server", e


    def on_created(self, event):
        self.catch_all(event, 'NEW')

    def on_modified(self, event):
        self.catch_all(event, 'MOD')


def main(ip,monitoringfolder,monitoringfile):
    global GLOBAL_IP
    GLOBAL_IP = ip
    print "Starting up"
    global MONITORING_FOLDER, FILE_TO_MONITOR
    MONITORING_FOLDER = monitoringfolder
    FILE_TO_MONITOR = monitoringfile
    observer = Observer()
    event_handler = MyHandler()
    observer.schedule(event_handler, MONITORING_FOLDER, recursive=False)
    observer.start()
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        observer.stop()

if __name__ == '__main__':
    main("localhost", "/var/log","auth.log")

