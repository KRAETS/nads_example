import json
import urllib2

DATA_STORE_ADDRESS = "localhost:8002"

def search(address, query):
    """Perform a serach in the specified address, with the specified query"""
    pair1 = ("mrlegit","thetrueuser")
    dummylist = []
    dummylist.append(pair1)
    return dummylist

def store_result(type, date, info, additional_info):
    """Sends a result for storage in the data ret manager"""
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
        print "Could not store result", e


