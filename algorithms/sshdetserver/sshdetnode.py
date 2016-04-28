import json
import os
import socket
import time
import urllib2
import signal
import logging
import dummy_data_retrieval as dr
from datetime import datetime, date
from flask import Flask
from flask import request
import ipblocksys as ip_tools
from watchdog.events import FileSystemEventHandler
from watchdog.observers import Observer
from login import Login
LOG_FILENAME = 'example.log'
dataaddr = "localhost:8002"
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
    logging.debug( "Blocking"+str(ip))
    if ip == "127.0.0.1" or ip == "localhost":
        logging.debug( "Not blocking localhost")
        return "OK"
    ip_tools.block(str(ip))
    return 'Ok'


@app.route('/',methods=['GET'])
def HELLO():
    logging.debug( "Hello")
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
    logging.debug( "Analyzing login!!")
    try:
        time.sleep(5)
        global LAST_CHECK
        #Query the data
        querystring = 'SELECT \ ALL*{protocol,portnumber,status,id,ip_address,datetime,name} \ ' \
                      'from \ ALL/{protocol,portnumber,status,id,ip_address} \ ' \
                      'where \ ALL*name*_:servername \ like "'+ socket.gethostname()+'" ' \
                      'and \ ALL*protocol*_:host \ like "*'+supported_protocols[protocol]+'*" ' \
                      'and  ( \ ALL*status \ like "*ailed*" or \ ALL*status \ like "*Accepted*" )'
        # query_url = 'http://localhost:9200/_kql?limit=10000&kql='
        # completequery = query_url + urllib.quote(querystring, safe='')
        # logging.debug( "Making query", completequery
        # # req = urllib2.Request(completequery)
        # results = None
        # try:
        #     response = requests.get(completequery)
        #
        #     logging.debug(things = response.text
        #
        #     # logging.debug(things = response.read()
        #     results = json.loads(logging.debug(things)
        #     # logging.debug( results
        # except Exception as e:
        #     logging.debug( "Could not open kql server", e
        #     exit(0)
        # logging.debug( "Got results"
        # Sort by time
        dr.DATA_RET_SERVER_ADDRESS = "localhost:8002"
        reslist = dr.search(None,querystring)
        usefulentries = []
        logging.debug( "Sorting reslist")#, reslist
        for item in reslist:
            entry = item
            try:
                s = entry["Date"]
                usefulentries.append(entry)
            except Exception as e:
                try:
                    s = entry["Date"][0]
                except Exception as e:
                    logging.debug( "Useless")
                    pass
                # logging.debug( "Useless"
                # logging.debug( entry
        logging.debug( "Changing time")#, usefulentries
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
                    logging.debug( "Problem"+str(e))
            # logging.debug( entry["Date"]
        usefulentries.sort(key=lambda x: x["Date"], reverse=True)
        #logging.debug( usefulentries

        # Check if we have to configure for the first time
        if LAST_CHECK is None:
            # If starting up, set the last entry as the last check (oldest)
            LAST_CHECK = usefulentries[len(usefulentries)-1]

        data = None
        # Compare last check with first result to see if it is the same thing or not.  Simple quick check

        #Analyze each one
        usefulentries.reverse()
        logging.debug( "Analyzing each entry.  Last checked was"+ str(LAST_CHECK)+ str(LAST_CHECK["Date"]))
        for entry in usefulentries:
            if LAST_CHECK["Date"] >= entry["Date"]:
                continue
            # logging.debug( "Entry is correct date", entry
            data = Login(False, "", "", "")
            status = ""
            try:
                status = entry["Status"][0].lower()
            except Exception as e:
                status = entry["Status"].lower()

            if status == "failed":
                data.set_status(False)
            else:
                data.set_status(True)
            logging.debug("Status is:"+str(data.get_status()))
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
                logging.debug( "Could not document instance"+str(e))
            LAST_CHECK = usefulentries[0]

            #Send to the analysis server
            try:
                logging.debug("Contacting"+'http://' + GLOBAL_IP + '/addlogin')
                logging.debug(data)
                req = urllib2.Request('http://' + GLOBAL_IP + '/addlogin')
                req.add_header('Content-Type', 'application/json')
                response = urllib2.urlopen(req, json.dumps(data.__dict__))
                # logging.debug(str(response))
            except Exception as e:
                logging.debug( "Could not contact server"+str(e))
        logging.debug( "Exited")
        LAST_CHECK = usefulentries[len(usefulentries)-1]
    except Exception as e:
        logging.debug("There was a problem"+str(e))
    return

class MyHandler(FileSystemEventHandler):

    def catch_all(self, event, op):
        # logging.debug( "Caught something", event
        if event.is_directory:
            return

        filename = event.src_path

        extension = os.path.splitext(filename)[-1].lower()

        if FILE_TO_MONITOR in filename and op is 'MOD':
            try:
                analyzeLogin()
            except Exception as e:
                logging.debug( "Problem contacting server" +str(e))


    def on_created(self, event):
        self.catch_all(event, 'NEW')

    def on_modified(self, event):
        self.catch_all(event, 'MOD')


def signal_term_handler(a, b):
    """Function to handle sigterm and shutdown proceses"""
    logging.debug( "PROTOCOL ATTACK DETECTION NODE Successfully Killed")
    try:
        shutdown_server()
    except Exception as e:
        logging.debug( "Could not shut down gracefully"+ str(e))
        exit(1)
    exit(0)


def main(ip, monitoringfolder, monitoringfile, supportedprotocols, targetprotocol, dataaddress,nodeport):
    global GLOBAL_IP, observer, supported_protocols, protocol, MONITORING_FOLDER, FILE_TO_MONITOR, dataaddr
    # Set shutdown hooks
    signal.signal(signal.SIGTERM, signal_term_handler)
    signal.signal(signal.SIGINT, signal_term_handler)
    logging.basicConfig(filename=LOG_FILENAME, level=logging.DEBUG)
    dataaddr = dataaddress
    # Set the supported protocols
    if supportedprotocols is not None:
        supported_protocols = supportedprotocols
    else:
        logging.debug( "No supported protocols specified")
        exit(1)
    if targetprotocol is not None:
        protocol = targetprotocol
    else:
        logging.debug( "No target protocol specified")
        exit(1)

    # Set the ip to send requests to
    GLOBAL_IP = ip
    logging.debug("Starting up:"+str(ip)+str(monitoringfile)+str(monitoringfolder))

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
    app.run(host='0.0.0.0', port=int(float(nodeport)))

if __name__ == '__main__':
    # Default startup
    main("localhost:8003", "/var/log", "auth.log", supported_protocols, "SSH")

