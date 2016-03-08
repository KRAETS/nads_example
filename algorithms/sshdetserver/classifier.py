import copy
from notidummy import notify_both

pair = ("", -1)


def remove_bruteforcer(item):
    if item.get_client() is pair[0]:
        return True
    else:
        return False


def detection_function(sn, sn_1, h):
    result = sn > sn_1 and sn > h
    return result


class Classifier:
    def __init__(self, mu, k, h):
        self.mu = mu
        self.k = k
        self.h = h
        pass

    def check_singleton(self, epoch):
        global pair

        # Verify if we have singleton

        copy_of_ooc_events = copy.deepcopy(epoch.get_history_events())

        hostmap = {}
        # Calculate fails per host
        for event in copy_of_ooc_events:
            for login in event.get_logins():
                if login.get_status() is False:
                    if login.get_client() not in hostmap.keys():
                        hostmap[login.get_client()] = 0
                    hostmap[login.get_client()] += 1
        # Calculate host with most fails
        for key in hostmap.keys():
            if hostmap[key] > pair[1]:
                pair = (key, hostmap[key])

        # Remove the host from all the events
        for event in copy_of_ooc_events:
            event.logins = filter(remove_bruteforcer, event.get_logins())

        # Set variables to re-simulate cusum

        GLOBAL_SN_1 = 0
        GLOBAL_SN = 0

        # now simulate the cusum
        for event in copy_of_ooc_events:
            # Calculate gfi
            gfi = event.calculate_gfi()
            print "Calculated GFI", gfi
            # Calculate zn
            zn = event.calculate_zn(self.mu, self.k)
            print "Calculated ZN", zn
            # SAVE OLD SN
            GLOBAL_SN_1 = GLOBAL_SN
            print "Global sn 1",GLOBAL_SN_1
            # Calculate new sn
            GLOBAL_SN += zn
            print "Global sn", GLOBAL_SN
            # Perform detection
            GLOBAL_DN = detection_function(GLOBAL_SN,GLOBAL_SN_1,self.h)
            print "Global dn", GLOBAL_DN
            # Set Event in control or not.  Has to be negated to reflect
            # in control = no detection and out of control = detection
            event.set_control(not GLOBAL_DN)

        # Check if the removed user was a bruteforcer by verifying if there are still any flagged events
        singleton = False
        for event in copy_of_ooc_events:
            if not event.get_control():
                break

        singleton = not singleton
        if singleton:
            return pair
        return None


    def process(self, epoch):
        result = self.check_singleton(epoch)
        if result is not None:
            # process singleton
            notify_both("hola"+str(result))
            pass

        else:
            #Analyze distributed attack
            pass








