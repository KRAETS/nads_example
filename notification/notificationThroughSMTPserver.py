import json
import re
import signal
import smtplib
import sys
from flask import Flask, request

# --------------------------------------------------------- Variables
print 'Notification Script started'
args = sys.argv
size = 1024
cellphoneComp = {'att': 'txt.att.net', 'att-cingular': 'mmode.com', 'sprint': 'messaging.sprintpcs.com',
                 'claro': 'vtexto.com', 'openMobile': 'email.openmobilepr.com', 'verizon': 'vtext.com'}
emailComp = {'gmail': 'smtp.gmail.com', 'yahoo': 'smtp.mail.yahoo.com', 'hotmail': 'smtp.live.com',
             'ece.uprm': 'smtps.ece.uprm.edu'}
serverdatacheck = True
email = ''
serverhost = ''
smtp = None
algs = dict()
validalgs = True

# ------------------------------------------------------------ Set-up
def setup():
    """Extracts smtp login and users to notify information."""
    try:
        print "Starting setup"
        global email, serverhost, serverdatacheck, validalgs, algs

        if len(args) > 1:  # ------- text and email set-up
            data = dict()
            if len(args) > 1:  # user information
                print "\tLoading argument", args[1]
                data = json.loads(args[1])

            if len(args) > 2:  # valid args list information
                print "\tLoading algorithms list", args[2]
                argslist = json.loads(args[2])
                for a in argslist["List"]:
                    #print "Loading", a
                    algs[a] = []
            else:
                validalgs = False
                print 'WARNING: No valid algorithms were received'

            if len(args) > 3:  # email and password information
                print 'Loading email'
                email = args[3]
                if len(args) > 4:
                    # print 'Loading email password'
                    print "Loading SMTP server"
                    serverhost = args[4]
                else:
                    # print "Could not get server host"
                    print "Could not get server data"
                    serverdatacheck = False
            else:
                serverdatacheck = False

            #print "Interpreting users  information"
            #print "Data", data
            for key in data:
                #print "Evaluating", key
                if 'phonenumber' in data.get(key):  # text set-up
                    num = data.get(key)['phonenumber'].replace('-', '')
                    num = data.get(key)['phonenumber'].replace(' ', '')
                    num = data.get(key)['phonenumber'].replace('(', '')
                    num = data.get(key)['phonenumber'].replace(')', '')
                    num = data.get(key)['phonenumber'].replace('+', '')
                    #print "Phone num", num
                    if num.isdigit() and len(num) == 10:
                        nflag = False
                        number = []
                        #print "Preparing to evaluate provider"
                        if data.get(key)['phoneprovider'].lower() in cellphoneComp:
                            #print "Found provider"
                            if data.get(key)['phoneprovider'].lower() == 'tmobile':
                                number = '+1' + num + '@' + cellphoneComp[data.get(key)['phoneprovider'].lower()]
                            else:
                                number = num + '@' + cellphoneComp[data.get(key)['phoneprovider'].lower()]
                            nflag = True
                        #print "Flag", nflag
                        if nflag:
                            #print "Preparing to check algs"
                            listtocheck = []
                            listtocheck = json.loads(data.get(key)['notifiablealgorithms'])
                            #listtocheck = data.get(key)['notifiablealgorithms']
                            #print listtocheck
                            for a in listtocheck:
                                #print "Checking", a
                                if a in algs.keys():
                                    #print "Appending dumber"
                                    algs[a].append(number)
                        #print "algs", algs
                    else:
                        print 'WARNING: \"' + data.get(key)[
                            'phonenumber'] + '\" phone number is incorrect and was not added to the notification list'

                if 'email' in data.get(key):  # email set-up
                    if re.match(r"^[A-Za-z0-9\.\+_-]+@[A-Za-z0-9\._-]+\.[a-zA-Z]*$(\.[a-zA-Z]+)?", data.get(key)['email']):
                        listtocheck = []
                        listtocheck = json.loads(data.get(key)['notifiablealgorithms'])
                        #listtocheck = data.get(key)['notifiablealgorithms']
                        for a in listtocheck:
                            if a in algs.keys():
                                algs[a].append(data.get(key)['email'])
                    else:
                        print 'WARNING: \"' + data.get(key)[
                            'email'] + '\" is not a valid email format and was not added to the notification list'
        else:
            print 'Error: No arguments were found and Notification Script couldn\'t start'
            sys.exit(1)
    except Exception as e:
        print "ERROR: There was a problem with the setup", e
        sys.exit(1)

    print "Initial setup Completed"

# --------------------------------------------- termination signal
def signal_term_handler(a, b):
    """Handling of termination signals"""
    global smtp
    try:
        shutdown_server()
        print "Notification Module Successfully Killed"

    except Exception as e:
        print "Could not shut down", e
    smtp.quit()
    sys.exit(0)

# ---------------------------------------------------- smtp set-up
def smtp_setup():
    """Sets up smtp configuration and login"""
    print "Smtp setup starting"
    global smtp, serverdatacheck, email, serverhost

    if serverdatacheck:
        smtp = smtplib.SMTP(serverhost)
    else:
        print "ERROR: Unable to set up the notification system. Email information is incomplete"
        shutdown_server()
        sys.exit(1)
    print "Finished smtp setup"

# --------------------------------------------------- Send message
def sendMessage(info, message):
    """Sends notification to users"""
    global validalgs, algs
    print "Send Message -----------------"
    if validalgs:

        if info in algs.keys():
            for each in algs[info]:
                Body = "From: %s" % email
                try:
                    smtp_setup()
                except Exception as e:
                    print "Could not login to send email", e
                try:
                    print "Sending to", info, each
                    smtp.sendmail(email, each, 'Subject: %s\n' % info + '\n' + message)
                except Exception as e:
                    print "Problem sending email", e
        else:
            print 'Algorithm not recognized'
    else:
        print 'No valid arguments were received in message'
    print 'Message sent'
    return "OK"

# -------------------------------------------------- server using flask
app = Flask(__name__)

@app.route('/<message>', methods=['GET'])
def message_receiver(message):
    """Receives notification to send to the user"""
    mes = str(message).split('**')
    print "Sending message"
    return sendMessage(mes[0], mes[1])

def shutdown_server():
    """Shuts down flask server"""
    return 'Server shutting down...'
    func = request.environ.get('werkzeug.server.shutdown')
    if func is None:
        raise RuntimeError('Not running with the Werkzeug Server')
    func()

@app.route('/shutdown', methods=['POST'])
def shutdown():
    """Receives notification to shut down flask"""
    shutdown_server()
    print 'Server successfully killed'

if __name__ == '__main__':
    print "Setting up signal handlers"
    signal.signal(signal.SIGTERM, signal_term_handler)
    signal.signal(signal.SIGINT, signal_term_handler)

    setup()
    smtp_setup()

    try:
        app.run(port=8000)
    except Exception as e:
        print 'ERROR:', e
        #sys.exit(-1)

    print 'Notification Script finished'
