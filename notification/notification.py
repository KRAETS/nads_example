from flask import Flask, request
import smtplib
import sys
import signal
import re
import json
import ast

# --------------------------------------------------------- Variables
print 'running'
args = sys.argv  # argv(1) json string
numbers = []
alg1 = []
alg2 = []
alg3 = []
size = 1024
celphoneComp = {'att': 'txt.att.net', 'att-cingular': 'mmode.com', 'sprint': 'messaging.sprintpcs.com',
                'claro': 'vtexto.com', 'tmobile': 'tmomail.net', 'openMobile': 'email.openmobilepr.com',
                'verizon': 'vtext.com'}
emailComp = {'gmail': 'smtp.gmail.com', 'yahoo': 'smtp.mail.yahoo.com', 'hotmail': 'smtp.live.com'}
email = 'rookyann@gmail.com'
password = 'oijfliwggfwtjqtt'

# ------------------------------------------------------------ Set-up
print 'set up'
if len(args) > 1:  # ------- text and email set-up
    # data = args[1].replace('\"','\'')
    data = dict()
    if len(args) > 1:
        data = json.loads(args[1])
    for key in data:
        if 'phonenumber' in data.get(key):  # text set-up
            num = data.get(key)['phonenumber'].replace('-', '')
            num = data.get(key)['phonenumber'].replace(' ', '')
            num = data.get(key)['phonenumber'].replace('(', '')
            num = data.get(key)['phonenumber'].replace(')', '')
            if num.isdigit():
                nflag = False
                if data.get(key)['phoneprovider'].lower() in celphoneComp:
                    if data.get(key)['phoneprovider'].lower() == 'tmobile':
                        numbers = '+1' + num + '@' + celphoneComp[data.get(key)['phoneprovider'].lower()]
                    else:
                        numbers = num + '@' + celphoneComp[data.get(key)['phoneprovider'].lower()]
                    nflag = True
                if nflag:
                    if '1' in data.get(key)['notifiablealgorithms']:
                        alg1.append(numbers)
                    elif '2' in data.get(key)['notifiablealgorithms']:
                        alg2.append(numbers)
                    elif '3' in data.get(key)['notifiablealgorithms']:
                        alg3.append(numbers)

        if 'email' in data.get(key):  # email set-up
            if re.match(r"^[A-Za-z0-9\.\+_-]+@[A-Za-z0-9\._-]+\.[a-zA-Z]*$", data.get(key)['email']):
                if '1' in data.get(key)['notifiablealgorithms']:
                    alg1.append(data.get(key)['email'])
                if '2' in data.get(key)['notifiablealgorithms']:
                    alg2.append(data.get(key)['email'])
                if '3' in data.get(key)['notifiablealgorithms']:
                    alg3.append(data.get(key)['email'])
else:
    print 'no args'
    sys.exit()

print alg1
print alg2
print alg3

# ---------------------------------------------------- smtp set-up
flag = False
start = email.lower().find('@') + 1
end = email.lower().find('.com')
emailprovider = email[start:end].lower()
option = []

if emailprovider in emailComp:
    option = [emailComp[emailprovider], 465]

try:
    smtp = smtplib.SMTP_SSL(option[0], option[1])
    flag = True
except smtplib.SMTPServerDisconnected:
    print 'Error: SMTPServerDisconnected'
except smtplib.SMTPResponseException:
    print 'Error: SMTPResponseException'
except smtplib.SMTPConnectError:
    print 'Error: SMTPConnectError'

if flag:
    if str(smtp.ehlo()[0]) == '250':
        try:
            smtp.login(email, password)
        except smtplib.SMTPAuthenticationError:
            print 'incorrect login credentials'

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
    app.run(port=2000)

# --------------------------------------------- termination signal
def signal_term_handler():
    print "Notification Module Successfully Killed"
    sys.exit(0)

signal.signal(signal.SIGTERM, signal_term_handler)
signal.signal(signal.SIGINT, signal_term_handler)
