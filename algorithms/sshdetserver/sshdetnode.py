import json
import os
import socket
import time
import urllib2
import signal
import dummy_data_retrieval as dr
from datetime import datetime, date
from flask import Flask
from flask import request
import ipblocksys as ip_tools
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

app = Flask(__name__)
observer = None
supported_protocols = \
{
    "SSH":"sshd",
    "SMTP":"smtpd"
}
protocol = None
GLOBAL_IP = None
MONITORING_FOLDER = None
FILE_TO_MONITOR = None
LAST_CHECK = None


@app.route('/blockip',methods=['POST'])
def blockip():
    ip = request.data
    print "Blocking", ip
    if ip == "127.0.0.1" or ip == "localhost":
        print "Not blocking localhost"
        return "OK"
    ip_tools.block(str(ip))
    return 'Ok'


@app.route('/',methods=['GET'])
def HELLO():
    print "Hello"
    return "Hello!!"


def shutdown_server():
    func = request.environ.get('werkzeug.server.shutdown')
    if func is None:
        raise RuntimeError('Not running with the Werkzeug Server')
    func()


@app.route('/shutdown', methods=['POST'])
def shutdown():
    shutdown_server()
    global observer
    observer.stop()
    return 'Server shut down...'

def analyzeLogin():
    global protocol, supported_protocols
    print "Analyzing login!!"
    try:
        time.sleep(5)
        global LAST_CHECK
        #Query the data
        querystring = 'SELECT \ ALL*{protocol,portnumber,status,id,ip_address,datetime,name} \ ' \
                      'from \ ALL/{protocol,portnumber,status,id,ip_address} \ ' \
                      'where \ ALL*name*_:servername \ like "'+ socket.gethostname()+'" ' \
                      'and \ ALL*protocol*_:host \ like "*'+supported_protocols[protocol]+'*" ' \
                      'and  ( \ ALL*status \ like "*Failed*" or \ ALL*status \ like "*Accepted*" )'
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
        print "Sorting reslist"#, reslist
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
        print "Changing time"#, usefulentries
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
        #print usefulentries

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
            # print "Entry is correct date", entry
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
                print response
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


def signal_term_handler(a, b):
    """Function to handle sigterm and shutdown proceses"""
    print "PROTOCOL ATTACK DETECTION NODE Successfully Killed"
    try:
        shutdown_server()
    except Exception as e:
        print "Could not shut down gracefully", str(e)
        exit(1)
    exit(0)


def main(ip, monitoringfolder, monitoringfile, supportedprotocols, targetprotocol):
    global GLOBAL_IP, observer, supported_protocols, protocol, MONITORING_FOLDER, FILE_TO_MONITOR
    # Set shutdown hooks
    signal.signal(signal.SIGTERM, signal_term_handler)
    signal.signal(signal.SIGINT, signal_term_handler)

    # Set the supported protocols
    if supportedprotocols is not None:
        supported_protocols = supportedprotocols
    else:
        print "No supported protocols specified"
        exit(1)
    if targetprotocol is not None:
        protocol = targetprotocol
    else:
        print "No target protocol specified"
        exit(1)

    # Set the ip to send requests to
    GLOBAL_IP = ip
    print "Starting up:", ip, monitoringfile, monitoringfolder

    # Set the folder/file to monitor
    MONITORING_FOLDER = monitoringfolder
    FILE_TO_MONITOR = monitoringfile
    # Initialize the observer object
    observer = Observer()
    # Initialize the file event handler
    event_handler = MyHandler()

    # Start the observer
    observer.schedule(event_handler, MONITORING_FOLDER, recursive=False)
    observer.start()
    # Start the blocking server
    app.run(host='0.0.0.0', port=8004)

if __name__ == '__main__':
    # Default startup
    main("localhost:8003", "/var/log", "auth.log", supported_protocols, "SSH")

