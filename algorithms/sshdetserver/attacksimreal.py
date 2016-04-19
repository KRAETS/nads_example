import time
import urllib2

# File containing the attack information
filename = "attack.txt"
# Ip of the server to attack
GLOBAL_IP = "localhost:8003"
distributed = False

# Total number of users when distributed
number_of_attack_users = 9
# Every 7 attempts a legit user fails by wrong password
legit_user_fail = 7
time_between_attacks = 0


def main(DISTRIBUTED):
    """Main function.  Executes an attack by simulating network connections
    DISTRIBUTED - True executes a distributed attack, false a singleton
    """
    if DISTRIBUTED is not None:
        global distributed
        distributed = DISTRIBUTED
    print "Starting attack"
    # Open the file containing a list with the type of attacks and frequencies
    attack_file = open(filename, 'r')

    failcount = 0
    successcount = 0
    currcount = 1

    for line in attack_file.readlines():
        # Ignore comment
        if "#" in line:
            continue
        # Execute a failed attempt if F  or Successful attempt if S
        else:
            if "F" in line or "f" in line:
                # produce failed attempt
                failcount += 1
                # Sign of a failed attempt
                json = None
                # Legitimate user fails
                if currcount % legit_user_fail is 0:
                    if currcount % 2 is 0:
                        # Mistaken password attempt
                        json = "{ \
                          \"status\": false,\
                          \"client\":\"127.0.0.1\",\
                          \"host\":\"somehost\",\
                          \"user\":\"pedro\"\
                        }"
                    else:
                        # Mistaken user account fail
                        json = "{ \
                          \"status\": false,\
                          \"client\":\"192.168.123.0\",\
                          \"host\":\"somehost\",\
                          \"user\":\"pedroa\"\
                        }"
                # Normal attack entry
                else:
                    json = "{ \
                      \"status\": false,\
                      \"client\":\""+"192.168.0."+str(currcount % number_of_attack_users)+"\",\
                      \"host\":\"somehost\",\
                      \"user\":\"theuser\"\
                    }"
                # Simulate a distributed attack by increasing the current count to create bogus 1-N users
                if distributed:
                    currcount += 1

                # Send request
                req = urllib2.Request('http://'+GLOBAL_IP+'/addlogin')
                req.add_header('Content-Type', 'application/json')
                response = urllib2.urlopen(req, json)

            # Successful request simulation
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
        # Print some statistics
        if (successcount + failcount) % 10 == 0:
            print "Stopping for event processing"
            time.sleep(time_between_attacks)

    print "Success:", successcount, "Fail:", failcount

# Call the main method
if __name__ == '__main__':
    main(False)
