import json
import urllib2
import requests

DATA_STORE_ADDRESS = "http://192.168.42.136:9200/_kql?limit=10000&kql="

DATA_RET_SERVER_ADDRESS = "192.168.42.136:8002"
def search(address, query):
    """Perform a serach in the specified address, with the specified query"""
    completequery = None
    if address is not None:
        completequery = address + urllib2.quote(query, safe='')
    else:
        completequery = DATA_STORE_ADDRESS + urllib2.quote(query, safe='')
    print "Making query", completequery
    # req = urllib2.Request(completequery)
    results = None
    print "Requesting", completequery

    try:
        response = requests.post("http://"+DATA_RET_SERVER_ADDRESS+"/getdata",query)

        printthings = response.text

        # printthings = response.read()
        results = json.loads(printthings)
        # print results
    except Exception as e:
        print "Could not open kql server", e
        exit(0)
    print "Got results"
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
    res["additional_info"] = additional_info
    try:
        request = "http://" + DATA_RET_SERVER_ADDRESS + "/" + "senddata"
        print "Requesting", request
        req = urllib2.Request(request)
        # req.add_header("Content-Type","application/json")
        req.add_data(json.dumps(res))
        response = urllib2.urlopen(req)
    except Exception as e:
        print "Could not store result", e


