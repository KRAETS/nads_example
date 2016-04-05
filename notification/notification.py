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

# ------------------------------------------------------------ Set-up
def setup():
    global email, password, emaildata

    print 'set up'
    if len(args) > 1:  # ------- text and email set-up
        data = dict()
        if len(args) > 1:
            # data = json.loads(args[1])
            f = open("demo")
            data = json.load(f)
        if len(args) > 2:
            email = args[2]
            print 'email'
            if len(args) > 3:
                password = args[3]
                print 'password'
            else:
                emaildata = False
        else:
            emaildata = False

        for key in data:
            if 'phonenumber' in data.get(key):  # text set-up
                num = data.get(key)['phonenumber'].replace('-', '')
                num = data.get(key)['phonenumber'].replace(' ', '')
                num = data.get(key)['phonenumber'].replace('(', '')
                num = data.get(key)['phonenumber'].replace(')', '')
                num = data.get(key)['phonenumber'].replace('+', '')
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
                        if '1' in data.get(key)['notifiablealgorithms']:
                            alg1.append(numbers)
                        elif '2' in data.get(key)['notifiablealgorithms']:
                            alg2.append(numbers)
                        elif '3' in data.get(key)['notifiablealgorithms']:
                            alg3.append(numbers)
                else:
                    print 'WARNING: ' + data.get(key)[
                        'name'] + ' phone number is incorrect and was not added to the notification list'

            if 'email' in data.get(key):  # email set-up
                if re.match(r"^[A-Za-z0-9\.\+_-]+@[A-Za-z0-9\._-]+\.[a-zA-Z]*$", data.get(key)['email']):
                    if '1' in data.get(key)['notifiablealgorithms']:
                        alg1.append(data.get(key)['email'])
                    if '2' in data.get(key)['notifiablealgorithms']:
                        alg2.append(data.get(key)['email'])
                    if '3' in data.get(key)['notifiablealgorithms']:
                        alg3.append(data.get(key)['email'])
                else:
                    print data.get(key)['email'] + 'is not a valid email format'
    else:
        print 'no args'
        sys.exit()

    print alg1
    print alg2
    print alg3

# --------------------------------------------- termination signal
def signal_term_handler():
    print "Notification Module Successfully Killed"
    shutdown_server()
    sys.exit(0)

signal.signal(signal.SIGTERM, signal_term_handler)
signal.signal(signal.SIGINT, signal_term_handler)

# ---------------------------------------------------- smtp set-up
smtp = None
def smtp_setup():
    global smtp
    if emaildata:
        flag = False
        start = email.lower().find('@')+1
        end = len(email.lower())
        emailprovider = email[start:end].lower()
        option = []
        for key in emailComp:
            if emailprovider in emailComp[key]:
                option = [emailComp[key], 465]
                break

        try:
            smtp = smtplib.SMTP_SSL(option[0], option[1])
            flag = True
        except smtplib.SMTPServerDisconnected:
            print 'Error: SMTPServerDisconnected'
        except smtplib.SMTPResponseException:
            print 'Error: SMTPResponseException'

        if flag:
            if str(smtp.ehlo()[0]) == '250':
                try:
                    res = smtp.login(email, password)
                except smtplib.SMTPAuthenticationError as e:
                    print 'Problem while attempting login',str(e)
    else:
        print "ERROR: Unable to set up the notification system. Email information is incomplete"
        signal_term_handler()

# --------------------------------------------------- Send message
def sendMessage(info, message):
    print "message-----------------"
    if info == "1":
        for each in alg1:
            smtp.sendmail(email, each, 'Subject: \n' + message)
    elif info == "2":
        for each in alg2:
            smtp.sendmail(email, each, 'Subject: \n' + message)
    elif info == "3":
        for each in alg3:
            smtp.sendmail(email, each, 'Subject: \n' + message)
    else:
        return 'algorithm not recognized'
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
    setup()
    smtp_setup()
    app.run(port=3000)




