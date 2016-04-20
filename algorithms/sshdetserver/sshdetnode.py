import json
import os
import requests
import socket
import threading
import time
import urllib
import urllib2
import dummy_data_retrieval as dr
from datetime import datetime, date

from watchdog.events import FileSystemEventHandler
from watchdog.observers import Observer

from login import Login

if os.name != "nt":
    import fcntl
    import struct

    def get_interface_ip(ifname):
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        return socket.inet_ntoa(fcntl.ioctl(s.fileno(), 0x8915, struct.pack('256s',
                                 ifname[:15]))[20:24])

def get_lan_ip():
    ip = socket.gethostbyname(socket.gethostname())
    if ip.startswith("127.") and os.name != "nt":
        interfaces = [
            "eth0",
            "eth1",
            "eth2",
            "wlan0",
            "wlan1",
            "wifi0",
            "ath0",
            "ath1",
            "ppp0",
            ]
        for ifname in interfaces:
            try:
                ip = get_interface_ip(ifname)
                break
            except IOError:
                pass
    return ip

GLOBAL_IP = None
MONITORING_FOLDER = "/var/log"
FILE_TO_MONITOR = "mylog"
KQL_SERVER = "localhost:9200/"
LAST_CHECK = None
def analyzeLogin():
    print "Analyzing login!!"

    try:
        time.sleep(2)
        global LAST_CHECK
        #Query the data
        querystring = 'SELECT \ ALL*{protocol,portnumber,status,id,ip_address,datetime} \ from \ ALL/{protocol,portnumber,status,id,ip_address} \ where ( \ ALL*status \ like "*Failed*" or \ ALL*status \ like "*Accepted*" )'
        # query_url = 'http://localhost:9200/_kql?limit=10000&kql='
        # completequery = query_url + urllib.quote(querystring, safe='')
        # print "Making query", completequery
        # # req = urllib2.Request(completequery)
        # results = None
        # try:
        #     response = requests.get(completequery)
        #
        #     printthings = response.text
        #
        #     # printthings = response.read()
        #     results = json.loads(printthings)
        #     # print results
        # except Exception as e:
        #     print "Could not open kql server", e
        #     exit(0)
        # print "Got results"
        # Sort by time
        reslist = dr.search(None,querystring)
        print 'y'
        usefulentries = []
        print "Sorting reslist", reslist
        for item in reslist:
            entry = item
            try:
                s = entry["Date"]
                usefulentries.append(entry)
            except Exception as e:
                try:
                    s = entry["Date"][0]
                except Exception as e:
                    print "Useless"
                    pass
                # print "Useless"
                # print entry
        print "Changing time", usefulentries
        for entry in usefulentries:
            try:
                d = entry["Date"]
                entry["Date"] = datetime.strptime(entry["Date"], '%b %d %H:%M:%S')
                entry["Date"] = entry["Date"].replace(year=date.today().year)
            except Exception as e:
                try:
                    d = entry["Date"][0]
                    entry["Date"] = datetime.strptime(entry["Date"][0], '%b %d %H:%M:%S')
                    entry["Date"] = entry["Date"].replace(year=date.today().year)
                except Exception as e:
                    print "Problem", e
            # print entry["Date"]
        usefulentries.sort(key=lambda x: x["Date"], reverse=True)
        print usefulentries

        # Check if we have to configure for the first time
        if LAST_CHECK is None:
            # If starting up, set the last entry as the last check (oldest)
            LAST_CHECK = usefulentries[len(usefulentries)-1]

        data = None
        # Compare last check with first result to see if it is the same thing or not.  Simple quick check

        #Analyze each one
        usefulentries.reverse()
        print "Analyzing each entry.  Last checked was", LAST_CHECK, LAST_CHECK["Date"]
        for entry in usefulentries:
            if LAST_CHECK["Date"] >= entry["Date"]:
                continue
            print "Entry is correct date", entry
            data = Login(False, "", "", "")
            try:
                if entry["Status"][0] == "Failed":
                    data.set_status(False)
                else:
                    data.set_status(True)
            except Exception as e:
                if entry["Status"] == "Failed":
                    data.set_status(False)
                else:
                    data.set_status(True)

            data.set_host(get_lan_ip())

            data.set_protocol("SSH")
            try:
                data.set_user(entry["UserName"][0])
            except Exception:
                data.set_user(entry["UserName"])

            try:
                data.set_client(entry["ClientIp"][0])
            except Exception as e:
                data.set_client(entry["ClientIp"])


            #Document the occurrence
            try:
                f = open("results.txt", "w")
                f.write(str(data)+" yes\n")
                f.close()
            except Exception as e:
                print "Could not document instance", e
            LAST_CHECK = usefulentries[0]

            #Send to the analysis server
            try:
                print "Contacting"+'http://' + GLOBAL_IP + '/addlogin'
                req = urllib2.Request('http://' + GLOBAL_IP + '/addlogin')
                req.add_header('Content-Type', 'application/json')
                response = urllib2.urlopen(req, json.dumps(data.__dict__))
                # print response
            except Exception as e:
                print "Could not contact server", e
        print "Exited"
        LAST_CHECK = usefulentries[len(usefulentries)-1]
    except Exception as e:
        print "There was a problem", e
    return

class MyHandler(FileSystemEventHandler):

    def catch_all(self, event, op):
        # print "Caught something", event
        if event.is_directory:
            return

        filename = event.src_path

        extension = os.path.splitext(filename)[-1].lower()

        if FILE_TO_MONITOR in filename and op is 'MOD':
            try:
                analyzeLogin()
            except Exception as e:
                print "Problem contacting server", e


    def on_created(self, event):
        self.catch_all(event, 'NEW')

    def on_modified(self, event):
        self.catch_all(event, 'MOD')


def main(ip,monitoringfolder,monitoringfile):
    global GLOBAL_IP
    GLOBAL_IP = ip
    print "Starting up:",ip,monitoringfile,monitoringfolder
    global MONITORING_FOLDER, FILE_TO_MONITOR
    MONITORING_FOLDER = monitoringfolder
    FILE_TO_MONITOR = monitoringfile
    observer = Observer()
    event_handler = MyHandler()
    observer.schedule(event_handler, MONITORING_FOLDER, recursive=False)
    observer.start()
    try:
        while True:
            time.sleep(3)
    except KeyboardInterrupt:
        print "Stopping observer"
        observer.stop()

if __name__ == '__main__':
    main("192.168.42.136:8003", "/var/log","auth.log")

