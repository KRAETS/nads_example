import time
import urllib2

filename = "attack.txt"
GLOBAL_IP = "localhost:8003"
distributed = False
number_of_attack_users = 9
legit_user_fail = 7 #Every 15 attempts a legit user fails by wrong password

def main(DISTRIBUTED):
    if DISTRIBUTED is not None:
        global distributed
        distributed = DISTRIBUTED
    print "Starting attack"
    attack_file = open(filename, 'r')
    failcount = 0
    successcount = 0
    currcount = 0

    for line in attack_file.readlines():
        if "#" in line:
            continue
        else:
            if "F" in line or "f" in line:
                # produce failed attempt
                failcount += 1
                #Sign of a failed attempt
                json = None
                if currcount % legit_user_fail is 0:
                    if currcount % 2 is 0:
                        #Mistaken password attempt
                        json = "{ \
                          \"status\": false,\
                          \"client\":\"mrlegit\",\
                          \"host\":\"somehost\",\
                          \"user\":\"thetrueuser\"\
                        }"
                    else:
                        #Mistaken user account fail
                        json = "{ \
                          \"status\": false,\
                          \"client\":\"mrlegit\",\
                          \"host\":\"somehost\",\
                          \"user\":\"thetrueuser1\"\
                        }"
                else:
                    json = "{ \
                      \"status\": false,\
                      \"client\":\""+str(currcount % number_of_attack_users)+"\",\
                      \"host\":\"somehost\",\
                      \"user\":\"theuser\"\
                    }"
                #Simulate a distributed attack with 0-2 users
                if distributed:
                    currcount += 1


                req = urllib2.Request('http://'+GLOBAL_IP+'/addlogin')
                req.add_header('Content-Type', 'application/json')
                response = urllib2.urlopen(req, json)

            elif "S" in line or "s" in line:
                # produce successful attempt
                successcount += 1
                json = "{ \
                  \"status\": true,\
                  \"client\":\"sometrueip\",\
                  \"host\":\"somehost\",\
                  \"user\":\"thetrueuser\"\
                }"
                req = urllib2.Request('http://'+GLOBAL_IP+'/addlogin')
                req.add_header('Content-Type', 'application/json')
                response = urllib2.urlopen(req, json)
                pass
            else:
                continue
        if (successcount+failcount) % 10 == 0:
            print "Stopping for event processing"
            time.sleep(0)

    print "Success:", successcount, "Fail:", failcount
if __name__ == '__main__':
    main(True)
