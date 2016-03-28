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
def analyzeLogin():
    data = Login(False,"192.168.0.123","ece","cruzpol")
    f = open("results.txt","w")
    f.write("yes")
    f.close()
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
                req = urllib2.Request('http://'+GLOBAL_IP+':5000/addlogin')
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

