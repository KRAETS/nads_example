from flask import Flask
import smtplib
import sys
import signal
import re

#argv(1) numbers//companies//alg, argv(3) emails//alg
#--------------------------------------------------------- Variables
args = sys.argv
numbers=[]
alg1=[]
alg2=[]
alg3=[]
size = 1024 
celphoneComp = {'att':'txt.att.net','att-cingular':'mmode.com','sprint':'messaging.sprintpcs.com','claro':'vtexto.com','tmobile':'tmomail.net','openMobile':'email.openmobilepr.com','verizon':'vtext.com'}
emailComp = {'gmail':'smtp.gmail.com','yahoo':'smtp.mail.yahoo.com','hotmail':'smtp.live.com'}
email = '<insert email>'
password = '<insert email password>'
#------------------------------------------------------------ Set-up
if len(args) > 1:   #------- text set-up
    data = args[1].split(',')
    for info in data:
        subinfo = info.split('//')
        num = subinfo[0].replace('-','') 
        if num.isdigit():
            nflag = False
            if subinfo[1].lower() in celphoneComp:
                numbers = num+'@'+celphoneComp[subinfo[1].lower()]
                nflag = True
            if nflag:
                if subinfo[2] == "1":
                    alg1.append(numbers)
                elif subinfo[2] == "2":
                    alg2.append(numbers)
                else:
                    alg3.append(numbers)
else:
    print 'no args'
    sys.exit()
    
if len(args) > 2:    #------ email set-up
    data = args[2].split(',')
    for info in data:
        subinfo = info.split('//')
        if re.match(r"^[A-Za-z0-9\.\+_-]+@[A-Za-z0-9\._-]+\.[a-zA-Z]*$", subinfo[0]):
            if subinfo[1] == "1":
                alg1.append(subinfo[0])
            elif subinfo[1] == "2":
                alg2.append(subinfo[0])
            else:
                alg3.append(subinfo[0])
print alg1
print alg2
print alg3            

#---------------------------------------------------- smtp set-up
flag = False
start = email.lower().find('@') + 1
end = email.lower().find('.com')
emailprovider = email.lower()[start:end]
option = []

if emailprovider in emailComp:
    option = [emailComp[emailprovider], 465]
    
try:
   smtp =  smtplib.SMTP_SSL(option[0], option[1])
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

#--------------------------------------------------- Send message
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
        return "algorithm not recognized"
    return "message sent"
    
#-------------------------------------------------- server using flask
app = Flask(__name__)

@app.route('/<message>')
def hello_world(message):
    data = str(message).split('**')
    return sendMessage(data[0], data[1]);

if __name__ == '__main__':
    app.run(port=2000)

#--------------------------------------------- termination signal
def signal_term_handler(signal, frame):
    print "Notification Module Successfully Killed"
    sys.exit(0)

signal.signal(signal.SIGTERM, signal_term_handler)
signal.signal(signal.SIGINT, signal_term_handler)



