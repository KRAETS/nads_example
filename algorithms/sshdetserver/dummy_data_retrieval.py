import urllib2
import json

DATA_STORE_ADDRESS = "localhost:8002"

def search(address, query):
    pair1 = ("mrlegit","thetrueuser")
    dummylist = []
    dummylist.append(pair1)
    return dummylist

def store_result(type, date, info, additional_info):
    res = {}
    res["type"] = type
    res["date"] = date
    res["info"] = info
    res["additional_info"] = additional_info
    try:
        request = "http://" + DATA_STORE_ADDRESS + "/" + "senddata"
        req = urllib2.Request(request)
        # req.add_header("Content-Type","application/json")
        req.add_data(json.dumps(res))
        response = urllib2.urlopen(req)
    except Exception as e:
        print e


