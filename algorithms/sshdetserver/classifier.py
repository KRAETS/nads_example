import copy
import editdistance
from notidummy import notify_both
from dummy_data_retrieval import *

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

    def analyze_past_history(self, epoch):
        epochclone = copy.deepcopy(epoch)
        # Get past successful logins
        past_success_logins = search("ip", "query")
        # Filter out failures according to past logins
        legit_users_filtering_function = def dummy(test_login):
            for login in past_success_logins:
                if test_login.get_client() is login[0] and test_login.get_user() is login[1]:
                    return True
            return False

        for event in epochclone:
            # Filter out the legit users
            event.logins = filter(legit_users_filtering_function,event.get_logins())

        legit_users_filtering_function = def dummy2(test_login):
            for login in past_success_logins:
                if test_login.get_client() is login[0] and editdistance.eval(login[1],test_login.get_user()) is 1:
                    return True
            return False


        pass

    def analyze_mistypes(self, epoch):
        pass

    def process(self, epoch):
        result = self.check_singleton(epoch)
        if result is not None:
            # process singleton
            notify_both("hola"+str(result))

        else:
            newepoch = self.analyze_past_history(epoch)
            print newepoch
            newepoch = self.analyze_mistypes(epoch)
            print newepoch

            # Analyze distributed attack
            pass








