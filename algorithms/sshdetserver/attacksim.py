import urllib2
import time

filename = "attack.txt"
GLOBAL_IP = "localhost"
if __name__ == '__main__':
    "Print starting attack"
    attack_file = open(filename, 'r')
    failcount = 0
    successcount = 0

    for line in attack_file.readlines():
        if "#" in line:
            continue
        else:
            if "F" in line or "f" in line:
                # produce failed attempt
                failcount += 1
                json = "{ \
                  \"status\": false,\
                  \"client\":\"someip\",\
                  \"host\":\"somehost\",\
                  \"user\":\"theuser\"\
                }"
                req = urllib2.Request('http://'+GLOBAL_IP+':5000/addlogin')
                req.add_header('Content-Type', 'application/json')
                response = urllib2.urlopen(req, json)

            elif "S" in line or "s" in line:
                # produce successful attempt
                successcount += 1
                json = "{ \
                  \"status\": true,\
                  \"client\":\"someip\",\
                  \"host\":\"somehost\",\
                  \"user\":\"theuser\"\
                }"
                req = urllib2.Request('http://'+GLOBAL_IP+':5000/addlogin')
                req.add_header('Content-Type', 'application/json')
                response = urllib2.urlopen(req, json)
                pass
            else:
                continue
        if (successcount+failcount) % 10 == 0:
            print "Stopping for event processing"
            time.sleep(1)

    print "Success:", successcount, "Fail:", failcount
