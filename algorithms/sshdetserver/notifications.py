import urllib2
import dummy_data_retrieval as dr
import time
import json
import logging

NOTIFICATION_SYSTEM_ADDRESS = "http://127.0.0.1:8000/"
ALGORITHM_NAME = "sshdetectionserver"
TYPE = "SSH"
LOG_FILENAME = 'example.log'
logging.basicConfig(filename=LOG_FILENAME, level=logging.DEBUG)

def notify_both(message):
    try:
        if message["type"] is not None:
            if message["type"] == "Singleton":
                # f = open("singletonresults.txt","a+")
                # f.write("yes\n"+str(message)+"\n")
                # f.close()
                #Notify of the event
                try:
                    request = NOTIFICATION_SYSTEM_ADDRESS + ALGORITHM_NAME + "**" + urllib2.quote("Singleton Attack Detected on "+str(message["data"][2]),safe='')
                    req = urllib2.Request(request)
                    response = urllib2.urlopen(req)
                    logging.debug(response)
                except Exception as e:
                    logging.debug( "Could not send message:"+str(e))


            elif message["type"] == "Distributed":
                # f = open("distributedresults.txt", "a+")
                # f.write("yes\n"+str(message)+"\n")
                # f.close()
                try:
                    ips = ""
                    for cluster in message["data"]:
                        ips+= cluster[0].replace("-host", "") + " "

                    request = NOTIFICATION_SYSTEM_ADDRESS + ALGORITHM_NAME + "**" + urllib2.quote("Distributed Attack Detected at "+ips,safe='')
                    logging.debug( "Notifying"+str( request))

                    req = urllib2.Request(request)
                    response = urllib2.urlopen(req)
                    logging.debug(str(response))
                except Exception as e:
                    logging.debug( "Could not send message:"+str(e))
    except Exception as e2:
        try:
            request = NOTIFICATION_SYSTEM_ADDRESS + ALGORITHM_NAME + "**" + urllib2.quote(str(message),
                                                                                          safe='')
    #        logging.debug( "Notifying", str(request))
            req = urllib2.Request(request)
            response = urllib2.urlopen(req)
            logging.debug(str(response))
        except Exception as e:
            logging.debug( "Could not send message:"+str(e)+str(e2))

