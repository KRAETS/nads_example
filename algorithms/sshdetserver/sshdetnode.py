import os
import time
import json
import urllib2
from login import Login

from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler

GLOBAL_IP = None

def analyzeLogin():
    data = Login(False,"192.168.0.123","ece","cruzpol")
    return data


class MyHandler(FileSystemEventHandler):

    def catch_all(self, event, op):
        print "Caught something", event
        if event.is_directory:
            return

        filename = event.src_path
        extension = os.path.splitext(filename)[-1].lower()
        if 'mylog' in filename and op is 'MOD':

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


def main(ip):
    global GLOBAL_IP
    GLOBAL_IP = ip
    print "Starting up"
    monitoring_folder = "/var/log/"
    observer = Observer()
    event_handler = MyHandler()
    observer.schedule(event_handler, monitoring_folder, recursive=False)
    observer.start()
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        observer.stop()
