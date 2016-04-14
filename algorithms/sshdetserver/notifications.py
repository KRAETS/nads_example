import urllib2
import dummy_data_retrieval as dr
import time
NOTIFICATION_SYSTEM_ADDRESS = "localhost:8000"
def notify_both(message):
    if message == "Singleton":
        f = open("singletonresults.txt","a+")
        f.write("yes\n"+str(message)+"\n")
        f.close()
        #Notify of the event
        try:
            request = NOTIFICATION_SYSTEM_ADDRESS + "/" + str(1) + "**" + "Singleton Attack Detected:"
            req = urllib2.Request(request)
            response = urllib2.urlopen(req)
        except Exception as e:
            print "Could not send message:",e


    elif message == "Distributed":
        f = open("distributedresults.txt", "a+")
        f.write("yes\n"+str(message)+"\n")
        f.close()
        try:
            request = NOTIFICATION_SYSTEM_ADDRESS + "/" + str(1) + "**" + "Distributed Attack Detected:"
            req = urllib2.Request(request)
            response = urllib2.urlopen(req)
        except Exception as e:
            print "Could not send message:", e
    else:
        print "notifying!!", message

