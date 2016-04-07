import json
import re
import signal
import smtplib
import sys

from flask import Flask, request

# --------------------------------------------------------- Variables
print 'running'
args = sys.argv  # argv(1) json string
numbers = []
alg1 = []
alg2 = []
alg3 = []
size = 1024
cellphoneComp = {'att': 'txt.att.net', 'att-cingular': 'mmode.com', 'sprint': 'messaging.sprintpcs.com',
                'claro': 'vtexto.com', 'tmobile': 'tmomail.net', 'openMobile': 'email.openmobilepr.com',
                'verizon': 'vtext.com'}
emailComp = {'gmail': 'smtp.gmail.com', 'yahoo': 'smtp.mail.yahoo.com', 'hotmail': 'smtp.live.com'}
emaildata = True
email = None
password = None
algs = dict()
validalgs = True

# ------------------------------------------------------------ Set-up
def setup():
    try:
        print "Starting setup"
        global email, password, emaildata, validalgs, algs

        print 'set up'
        if len(args) > 1:  # ------- text and email set-up
            data = dict()
            if len(args) > 1:
                print "Loading argument", args[1]
                data = json.loads(args[1])
                # f = open("demo")
                # data = json.load(f)

            if len(args) > 2:
                print "Loading alg list"
                jlist = json.loads(args[2])
                print jlist

                for a in jlist["list"]:
                    algs[a] = []
                print algs
            else:
                validalgs = False
                print 'no valid algorithms were received'

            if len(args) > 3:
                email = args[3]
                print 'email'
                if len(args) > 4:
                    password = args[4]
                    print 'password'
                else:
                    emaildata = False
            else:
                emaildata = False

            print "Interpreting phone numbers"
            print "Using ", data
            for key in data:
                print "Data", data
                if 'phonenumber' in data.get(key):  # text set-up
                    print "Filtering number"
                    print "Key",key
                    print "Data get key", data.get(key)

                    num = data.get(key)['phonenumber'].replace('-', '')
                    num = data.get(key)['phonenumber'].replace(' ', '')
                    num = data.get(key)['phonenumber'].replace('(', '')
                    num = data.get(key)['phonenumber'].replace(')', '')
                    num = data.get(key)['phonenumber'].replace('+', '')
                    print "Filtering done"
                    if num.isdigit() and len(num) == 10:
                        nflag = False
                        if data.get(key)['phoneprovider'].lower() in cellphoneComp:
                            if data.get(key)['phoneprovider'].lower() == 'tmobile':
                                numbers = '+1' + num + '@' + cellphoneComp[data.get(key)['phoneprovider'].lower()]
                            else:
                                numbers = num + '@' + cellphoneComp[data.get(key)['phoneprovider'].lower()]
                            nflag = True
                        if nflag:
                            #TODO make this a for instead of hardcoding
                            for a in data.get(key)['notifiablealgorithms']:
                                if a in algs.keys():
                                    algs[a].append(numbers)

                    else:
                        print 'WARNING: ' + data.get(key)[
                            'name'] + ' phone number is incorrect and was not added to the notification list'
                print "Interpreting emails"
                if 'email' in data.get(key):  # email set-up
                    if re.match(r"^[A-Za-z0-9\.\+_-]+@[A-Za-z0-9\._-]+\.[a-zA-Z]*$", data.get(key)['email']):
                        for a in data.get(key)['notifiablealgorithms']:
                            if a in algs.keys():
                                algs[a].append(data.get(key)['email'])

                    else:
                        print data.get(key)['email'] + 'is not a valid email format'
        else:
            print 'no args'
            sys.exit()
        print algs
        print "Finished initial setup"
    except Exception as e:
        print "There was a problem with the setup", e

# --------------------------------------------- termination signal
def signal_term_handler(a,b):
    print "Notification Module Successfully Killed"
    shutdown_server()
    sys.exit(0)

# ---------------------------------------------------- smtp set-up
smtp = None
def smtp_setup():
    print "Smtp setup starting"
    global smtp
    if emaildata:
        print "Evaluating smtp email"
        flag = False
        start = email.lower().find('@')+1
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
            print "COuld not establish ssl connection", str(e)

        print "Ssl established", flag
        if flag:
            print "Trying Login"
            if str(smtp.ehlo()[0]) == '250':
                try:
                    res = smtp.login(email, password)
                    print "Login result", res
                except Exception as e:
                    print 'Problem while attempting login', str(e)
    else:
        print "ERROR: Unable to set up the notification system. Email information is incomplete"
        signal_term_handler()
    print "Finished smtp setup"
# --------------------------------------------------- Send message
def sendMessage(info, message):
    global validalgs, algs
    print "message-----------------"
    if validalgs:
        if info in algs.keys():
            for each in algs[info]:
                smtp.sendmail(email, each, 'Subject: \n' + message)
        else:
            return 'algorithm not recognized'
    else:
        print 'no valid arguments were received'
    return 'message sent'


# -------------------------------------------------- server using flask
app = Flask(__name__)


@app.route('/<message>', methods=['GET'])
def hello_world(message):
    mes = str(message).split('**')
    return sendMessage(mes[0], mes[1])


def shutdown_server():
    func = request.environ.get('werkzeug.server.shutdown')
    if func is None:
        raise RuntimeError('Not running with the Werkzeug Server')
    func()


@app.route('/shutdown', methods=['POST'])
def shutdown():
    shutdown_server()
    return 'Server shutting down...'


if __name__ == '__main__':
    print "Setting up the signal handler"
    signal.signal(signal.SIGTERM, signal_term_handler)
    signal.signal(signal.SIGINT, signal_term_handler)

    print "Initial setup"
    setup()
    print "Initial setup done, Smtp setup"
    smtp_setup()

    print "Smtp setup done, flask startup"
    try:
        app.run(port=8000)
    except Exception as e:
        print "Could nto start flask", e
    print "Flask startup exited"




