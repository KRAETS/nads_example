import urllib2
import dummy_data_retrieval as dr
import time
import json
NOTIFICATION_SYSTEM_ADDRESS = "http://127.0.0.1:8000/"
ALGORITHM_NAME = "sshdetectionserver"
TYPE = "SSH"
def notify_both(message):
    try:
        if message["type"] is not None:
            if message["type"] == "Singleton":
                f = open("singletonresults.txt","a+")
                f.write("yes\n"+str(message)+"\n")
                f.close()
                #Notify of the event
                try:
                    request = NOTIFICATION_SYSTEM_ADDRESS + ALGORITHM_NAME + "**" + urllib2.quote("Singleton Attack Detected",safe='')
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
                    request = NOTIFICATION_SYSTEM_ADDRESS + ALGORITHM_NAME + "**" + urllib2.quote("Distributed Attack Detected",safe='')
                    print "Notifying", request
                    req = urllib2.Request(request)
                    response = urllib2.urlopen(req)
                    print response
                except Exception as e:
                    print "Could not send message:", e
    except Exception as e2:
        try:
            request = NOTIFICATION_SYSTEM_ADDRESS + ALGORITHM_NAME + "**" + urllib2.quote(str(message),
                                                                                          safe='')
            print "Notifying", request
            req = urllib2.Request(request)
            response = urllib2.urlopen(req)
            print response
        except Exception as e:
            print "Could not send message:", e, e2

