import copy

import editdistance
from igraph import *

from dummy_data_retrieval import *
from notidummy import notify_both

pair = ("", -1)




def detection_function(sn, sn_1, h):
    result = sn > sn_1 and sn > h
    return result


class Classifier:

    def __init__(self, mu, h, k):
        self.mu = mu
        self.k = k
        self.h = h
        self.current_epoch = None

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

        def remove_bruteforcer(item):
            clnt = item.get_client()
            if item.get_client() == pair[0]:
                return False
            else:
                return True

        # Remove the host from all the events
        for event in copy_of_ooc_events:
            event.logins = filter(remove_bruteforcer, event.get_logins())

        # Set variables to re-simulate cusum

        GLOBAL_SN_1 = 0
        GLOBAL_SN = 0
        zn = 0
        gfi = 0
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
                singleton = True
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
        def successful_previous_login_filter(test_login):
            for login in past_success_logins:
                if str(test_login.get_client()) == str(login[0]) and test_login.get_user() == login[1]:
                    print "Previous success detected", test_login.get_client(), test_login.get_user()
                    return False
            return True

        for event in epochclone.get_history_events():
            # Filter out the legit users
            event.logins = filter(successful_previous_login_filter,event.get_logins())

        def mistyped_successful_previous_login_filter(test_login):
            for login in past_success_logins:
                print "Edit distance",  editdistance.eval(login[1],test_login.get_user())
                print "Opposite", editdistance.eval(test_login.get_user(),login[1])
                print test_login.get_user(),login[1]
                print test_login.get_client() == login[0]
                print editdistance.eval(login[1],test_login.get_user()) == 1
                print editdistance.eval(login[1],test_login.get_user()) is 1
                print "For"
                if test_login.get_client() == login[0] and editdistance.eval(login[1],test_login.get_user()) == 1:
                    print "Mistype detected", test_login.get_client()
                    return False
            return True

        for event in epochclone.get_history_events():
            # Filter out the legit users
            event.logins = filter(mistyped_successful_previous_login_filter,event.get_logins())

        return epochclone

    def analyze_coordination_glue(self,epoch):
        graph = Graph()
        #Nodeset 1
        nodeset1 = set()
        for event in epoch.get_history_events():
            for login in event.get_logins():
                #add the remote host nodeset
                if not login.get_status():
                    # graph.add_vertex(login.get_client())
                    nodeset1.add(login.get_client())

        nodeset2 = set()
        #Nodeset 2
        for event in epoch.get_history_events():
            for login in event.get_logins():
                #add the host nodeset
                if not login.get_status():
                    # graph.add_vertex(login.get_host())
                    nodeset2.add(login.get_host())

        #Join Remotehost->localhost
        for vertex in nodeset1.union(nodeset2):
            graph.add_vertex(vertex)
        print graph
        print "Vertices", graph.vs["name"]
        for event in epoch.get_history_events():
            for login in event.get_logins():
                if not login.get_status():
                    remotehost = login.get_client()
                    localhost = login.get_host()
                    edge = (remotehost, localhost)
                    source = 1
                    destination = 1
                    for vertex in graph.vs["name"]:
                        if vertex == remotehost:
                            break
                        source += 1
                    for vertex in graph.vs["name"]:
                        if vertex == localhost:
                            break
                        destination += 1
                    graph.add_edge(source-1,destination-1)


                    # graph.add_edge(remotehost, localhost)
                    # graph.add_edges(edge)

        clusters = graph.clusters()

        visual_style = {}
        visual_style["vertex_size"] = 20
        # visual_style["vertex_color"] = [color_dict[gender] for gender in g.vs["gender"]]
        visual_style["vertex_label"] = graph.vs["name"]
        # visual_style["edge_width"] = [1 + 2 * int(is_formal) for is_formal in g.es["is_formal"]]
        visual_style["layout"] = layout
        visual_style["bbox"] = (300, 300)
        visual_style["margin"] = 20
        # plot(clusters, **visual_style)

        #Get the top target(centers of clusters)
        top = ("", 0)
        for cluster in clusters:
            print cluster
            for vertex in cluster:
                degree = graph.degree(vertex)
                if degree > top[1]:
                    top = (graph.vs["name"][vertex], degree)

        print top

        print clusters

        return top

    def process(self, epoch):
        print "Processing epoch", epoch
        self.current_epoch = epoch

        result = self.check_singleton(epoch)
        print "Result",result
        if result is not None:
            # process singleton
            notify_both("Singleton")

        else:
            print "Filtering out legitimate activity"
            newepoch = self.analyze_past_history(epoch)
            print "Done", newepoch
            print "Analyzing coordination glue"
            hitpair = self.analyze_coordination_glue(newepoch)
            notify_both("Distributed")
            print hitpair

