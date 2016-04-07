import urllib2
NOTIFICATION_SYSTEM_ADDRESS = "localhost:8000"
def notify_both(message):
    if message == "Singleton":
        f = open("singletonresults.txt","w+")
        f.write("yes")
        f.close()
        try:
            request = NOTIFICATION_SYSTEM_ADDRESS + "/" + str(1) + "**" + "Singleton Attack Detected:"
            req = urllib2.Request(request)
            response = urllib2.urlopen(req)
        except Exception as e:
            print "Could not send message:",e

    elif message == "Distributed":
        f = open("distributedresults.txt", "w+")
        f.write("yes")
        f.close()
        try:
            request = NOTIFICATION_SYSTEM_ADDRESS + "/" + str(1) + "**" + "Distributed Attack Detected:"
            req = urllib2.Request(request)
            response = urllib2.urlopen(req)
        except Exception as e:
            print "Could not send message:", e
    else:
        print "notifying!!", message

