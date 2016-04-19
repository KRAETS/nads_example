import urllib2
import dummy_data_retrieval as dr
import time
import json
NOTIFICATION_SYSTEM_ADDRESS = "http://127.0.0.1:8000/"
ALGORITHM_NAME = "sshdetectionserver"
def notify_both(message):
    if message["type"] == "Singleton":
        f = open("singletonresults.txt","a+")
        f.write("yes\n"+str(message)+"\n")
        f.close()
        #Notify of the event
        try:
            request = NOTIFICATION_SYSTEM_ADDRESS + ALGORITHM_NAME + "**" + urllib2.quote("Singleton Attack Detected"+json.dumps(message),safe='')
            req = urllib2.Request(request)
            response = urllib2.urlopen(req)
            print response
        except Exception as e:
            print "Could not send message:",e


    elif message["type"] == "Distributed":
        f = open("distributedresults.txt", "a+")
        f.write("yes\n"+str(message)+"\n")
        f.close()
        try:
            request = NOTIFICATION_SYSTEM_ADDRESS + ALGORITHM_NAME + "**" + urllib2.quote("Distributed Attack Detected"+json.dumps(message),safe='')
            print "Notifying", request
            req = urllib2.Request(request)
            response = urllib2.urlopen(req)
            print response
        except Exception as e:
            print "Could not send message:", e
    else:
        print "notifying!!", message

