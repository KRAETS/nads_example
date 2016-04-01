import json
import os
import time
import urllib2

from watchdog.events import FileSystemEventHandler
from watchdog.observers import Observer

from login import Login

GLOBAL_IP = None
MONITORING_FOLDER = "/var/log"
FILE_TO_MONITOR = "mylog"
KQL_SERVER = "localhost:9200/"
LAST_CHECK = None
def analyzeLogin():
    global LAST_CHECK
    #Query the data
    querystring = 'SELECT \ ALL*{protocol,portnumber,status,id,ip_address} \ from \ ALL/{protocol,portnumber,status,id,ip_address} \ where \ ALL*status \="Failed" or \ ALL*status \="Accepted"'
    query_url = 'http://localhost:9200/_kql?kql='
    completequery = query_url + querystring

    req = urllib2.Request(completequery)
    try:
        response = urllib2.urlopen(req)
    except Exception as e:
        print "Could not open kql server"
        return None

    #Sort by time

    #Compare last check with first result

    #if firstresult is lessthan or equal to last check then return none
    firstresult = "this"
    data = None
    if firstresult <= LAST_CHECK:
        return None
    else:
        data = Login(False,"192.168.0.123","ece","cruzpol")
        f = open("results.txt","w")
        f.write("yes")
        f.close()

    LAST_CHECK = time.time()
    if data is not None:
        return data
    else:
        return None

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
    main("localhost", "dummy","dummylog")

