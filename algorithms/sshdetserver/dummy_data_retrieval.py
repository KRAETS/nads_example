import json
import urllib2
import requests
import logging

DATA_STORE_ADDRESS = "http://192.168.42.136:9200/_kql?limit=10000&kql="

DATA_RET_SERVER_ADDRESS = "136.145.59.139:8002"

LOG_FILENAME = 'example.log'
logging.basicConfig(filename=LOG_FILENAME, level=logging.DEBUG)


def search(address, query):
    """Perform a serach in the specified address, with the specified query"""
    # req = urllib2.Request(completequery)
    results = None
    # print "Requesting", completequery

    try:
        # logging.debug(  "Asking for data", "http://"+DATA_RET_SERVER_ADDRESS+"/getdata")
        # logging.debug(  query)
        response = requests.post("http://"+DATA_RET_SERVER_ADDRESS+"/getdata",query)
        logging.debug(  "Response"+ str(response))
        printthings = response.text

        # printthings = response.read()
        results = json.loads(printthings)
        # print results
    except Exception as e:
        logging.debug(  "Could not open kql server"+str(e))
    logging.debug(  "Got results")
    dummylist = []
    reallist = []
    if results is not None:
        try:
            dummylist = results["hits"]["hits"]
        except Exception as e:
            dummylist = results["hits"]
        reallist = []
        for item in dummylist:
            reallist.append(item["_source"])
    return reallist

def store_result(type, date, info, additional_info):
    """Sends a result for storage in the data ret manager"""
    res = {}
    res["type"] = type
    res["date"] = date
    res["info"] = info
    res["additional_info"] = json.dumps(additional_info)
    try:
        request = "http://" + DATA_RET_SERVER_ADDRESS + "/" + "senddata"
        logging.debug(  "Requesting"+str(request))
        req = urllib2.Request(request)
        # req.add_header("Content-Type","application/json")
        req.add_data(str(res).replace("\"","").replace("'",""))
        response = urllib2.urlopen(req)
    except Exception as e:
        logging.debug(  "Could not store result"+ str(e))


