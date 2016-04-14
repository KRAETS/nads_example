import json
import re
import signal
import smtplib
import sys
from flask import Flask, request

# --------------------------------------------------------- Variables
print 'Notification Script started'
args = sys.argv  # argv(1) json string
size = 1024
cellphoneComp = {'att': 'txt.att.net', 'att-cingular': 'mmode.com', 'sprint': 'messaging.sprintpcs.com',
                 'claro': 'vtexto.com', 'tmobile': 'tmomail.net', 'openMobile': 'email.openmobilepr.com',
                 'verizon': 'vtext.com'}
emailComp = {'gmail': 'smtp.gmail.com', 'yahoo': 'smtp.mail.yahoo.com', 'hotmail': 'smtp.live.com'}
emaildatacheck = True
email = ''
password = ''
smtp = None
algs = dict()
validalgs = True

# ------------------------------------------------------------ Set-up
def setup():
    """Extracts smtp login information and users to notify information."""
    try:
        print "Starting setup"
        global email, password, emaildatacheck, validalgs, algs

        if len(args) > 1:  # ------- text and email set-up
            data = dict()
            if len(args) > 1:  # user information
                print "\tLoading argument", args[1]
                data = json.loads(args[1])

            if len(args) > 2:  # valid args list information
                print "\tLoading algorithms list", args[2]
                argslist = json.loads(args[2])

                for a in argslist["List"]:
                    algs[a] = []
            else:
                validalgs = False
                print 'WARNING: No valid algorithms were received'

            if len(args) > 3:  # email and password information
                email = args[3]
                print 'Loading email'
                if len(args) > 4:
                    password = args[4]
                    print 'Loading email password'
                else:
                    emaildatacheck = False
            else:
                emaildatacheck = False

            print "Interpreting users  information"
            for key in data:
                if 'phonenumber' in data.get(key):  # text set-up
                    num = data.get(key)['phonenumber'].replace('-', '')
                    num = data.get(key)['phonenumber'].replace(' ', '')
                    num = data.get(key)['phonenumber'].replace('(', '')
                    num = data.get(key)['phonenumber'].replace(')', '')
                    num = data.get(key)['phonenumber'].replace('+', '')

                    if num.isdigit() and len(num) == 10:
                        nflag = False
                        number = []
                        if data.get(key)['phoneprovider'].lower() in cellphoneComp:
                            if data.get(key)['phoneprovider'].lower() == 'tmobile':
                                number = '+1' + num + '@' + cellphoneComp[data.get(key)['phoneprovider'].lower()]
                            else:
                                number = num + '@' + cellphoneComp[data.get(key)['phoneprovider'].lower()]
                            nflag = True
                        if nflag:
                            for a in data.get(key)['notifiablealgorithms']:
                                if a in algs.keys():
                                    algs[a].append(number)
                    else:
                        print 'WARNING: \"' + data.get(key)[
                            'phonenumber'] + '\" phone number is incorrect and was not added to the notification list'

                if 'email' in data.get(key):  # email set-up
                    if re.match(r"^[A-Za-z0-9\.\+_-]+@[A-Za-z0-9\._-]+\.[a-zA-Z]*$", data.get(key)['email']):
                        for a in data.get(key)['notifiablealgorithms']:
                            if a in algs.keys():
                                algs[a].append(data.get(key)['email'])
                    else:
                        print 'WARNING: \"' + data.get(key)[
                            'email'] + '\" is not a valid email format and was not added to the notification list'
        else:
            print 'Error: No arguments were found and Notification Script couldn\'t start'
            sys.exit(-1)
    except Exception as e:
        print "There was a problem with the setup", e

    print "Initial setup Completed"

# --------------------------------------------- termination signal
def signal_term_handler(a, b):
    """Receives terminate signal and process it."""
    shutdown_server()
    print "Notification Module Successfully Killed"
    sys.exit(0)

# ---------------------------------------------------- smtp set-up
def smtp_setup():
    """Sets up smtp configuration and login"""
    print "Smtp setup starting"
    global smtp, emaildatacheck, email
    if emaildatacheck:
        print "Evaluating smtp email"
        flag = False
        start = email.lower().find('@') + 1
        end = len(email.lower())
        emailprovider = email[start:end].lower()
        option = []
        print "Finding provider"
        for key in emailComp:
            if emailprovider in emailComp[key]:
                option = [emailComp[key], 465]
                break
        print "Trying ssl"

        try:
            smtp = smtplib.SMTP_SSL(option[0], option[1])
            flag = True
        except Exception as e:
            print "Could not establish ssl connection", str(e)

        print "Ssl established: ", flag
        if flag:
            print "Trying smtp Login"
            if str(smtp.ehlo()[0]) == '250':
                try:
                    smtp.login(email, password)
                except Exception as e:
                    print 'Problem while attempting login ', str(e)
    else:
        print "ERROR: Unable to set up the notification system. Email information is incomplete"
        signal_term_handler()
    print "Finished smtp setup"

# --------------------------------------------------- Send message
def sendMessage(info, message):
    """Sends notification to users"""
    global validalgs, algs
    print "Send Message -----------------"
    if validalgs:
        if info in algs.keys():
            for each in algs[info]:
                smtp.sendmail(email, each, 'Subject: \n' + message)
        else:
            return 'Algorithm not recognized'
    else:
        return 'No valid arguments were received in message'
    return 'Message sent'

# -------------------------------------------------- server using flask
app = Flask(__name__)

@app.route('/<message>', methods=['GET'])
def message_receiver(message):
    """Receives notification to send to the user"""
    mes = str(message).split('**')
    return sendMessage(mes[0], mes[1])

def shutdown_server():
    """Shuts down flask"""
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
        print e

    print 'Notification Script finished'
