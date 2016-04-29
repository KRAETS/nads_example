import copy
import time
import ipblocksys as ip_tools
import editdistance
from igraph import *
import logging
LOG_FILENAME = 'example.log'
CLIENTPORT = 8004
import dummy_data_retrieval as dr
import notifications
pair = ("", -1, [])

supported_protocols = \
{
    "SSH":"sshd",
    "SMTP":"smtpd"
}



def detection_function(sn, sn_1, h):
    """Perform detection if accumulation is greater than previous and bigger than threshold H"""
    result = sn > sn_1 and sn > h
    return result


class Classifier:
    """Class that performs epoch attack type classification"""
    def __init__(self, mu, h, k, supportedprotocols, protocol, dataaddr, blockclientport):
        """Sets the default parameters
        mu mean of failed attempts
        h accumulation threshold
        k parameter to lower normal mean to <0 and make it discrete
        """
        logging.basicConfig(filename=LOG_FILENAME, level=logging.DEBUG)
        self.mu = mu
        self.k = k
        self.h = h
        self.current_epoch = None
        self.type = protocol
        self.set_supported_protocols(supportedprotocols)
        self.dataaddress = dataaddr
        dr.DATA_RET_SERVER_ADDRESS = self.dataaddress
        ip_tools.PORT = blockclientport

    def set_type(self, newtype):
        for type in supported_protocols.keys():
            if type == newtype:
                self.type = newtype
                return True
        return False

    @staticmethod
    def set_supported_protocols(protocols):
        global supported_protocols
        supported_protocols = protocols
        return

    def check_singleton(self, epoch):
        """Analyzes an epoch to determine if it is a singleton attack.  Returns pair if singleton"""
        global pair

        # Verify if we have singleton

        copy_of_ooc_events = copy.deepcopy(epoch.get_history_events())

        hostmap = {}
        hostattackmap = {}
        # Calculate fails per host
        for event in copy_of_ooc_events:
            for login in event.get_logins():
                if login.get_status() is False:
                    if login.get_client() not in hostmap.keys():
                        hostmap[login.get_client()] = 0
                        hostattackmap[login.get_client()] = []
                    hostmap[login.get_client()] += 1
                    hostattackmap[login.get_client()].append(login.get_host())

        # Calculate host with most fails
        for key in hostmap.keys():
            if hostmap[key] > pair[1]:
                pair = (key, hostmap[key], hostattackmap[key])

        def remove_bruteforcer(item):
            clnt = item.get_client()
            if item.get_client() == pair[0]:
                return False
            else:
                return True

        # Remove the bruteforcing host from all the events
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
            logging.debug( "Calculated GFI"+str(gfi))
            # Calculate zn
            zn = event.calculate_zn(self.mu, self.k)
            logging.debug( "Calculated ZN"+str(zn))
            # SAVE OLD SN
            GLOBAL_SN_1 = GLOBAL_SN
            logging.debug( "Global sn 1"+str(GLOBAL_SN_1))
            # Calculate new sn
            GLOBAL_SN += zn
            logging.debug( "Global sn"+str(GLOBAL_SN))
            # Perform detection
            GLOBAL_DN = detection_function(GLOBAL_SN, GLOBAL_SN_1, self.h)
            logging.debug( "Global dn"+str(GLOBAL_DN))
            # Set Event in control or not.  Has to be negated to reflect
            # in control = no detection and out of control = detection
            event.set_control(not GLOBAL_DN)

        # Check if the removed user was a bruteforcer by verifying if there are still any flagged events
        singleton = False
        for event in copy_of_ooc_events:
            if not event.get_control():
                singleton = True
                break
        # Return the pair containing the host and the # of attempts
        singleton = not singleton
        if singleton:
            return pair
        return None

    def analyze_past_history(self, epoch):
        """Analyzes an epoch to remove legitimate users, or legitimate users who put a wrong password/login"""
        epochclone = copy.deepcopy(epoch)
        # Get past successful logins
        query = 'SELECT \ ALL*{protocol,portnumber,status,id,ip_address,datetime} \ from' \
                ' \ ALL/{protocol,portnumber,status,id,ip_address} \ where ( \ ALL*status \ like "*Accepted*" ) ' \
                'and \ ALL*protocol*_:host \ like "*'+supported_protocols[self.type]+'*"'

        past_success_logins = dr.search(None, query)

        if len(past_success_logins) == 0:
            return epoch

        # Filter out failures according to past logins
        def successful_previous_login_filter(test_login):
            for login in past_success_logins:
                if str(test_login.get_client()) == str(login["ClientIp"]) and \
                                test_login.get_user() == login["UserName"]:
                    logging.debug( "Previous success detected"+str(test_login.get_client())+str(test_login.get_user()))
                    return False
            return True

        for event in epochclone.get_history_events():
            # Filter out the legit users
            event.logins = filter(successful_previous_login_filter, event.get_logins())

        # Filtering function for mistaken login
        def mistyped_successful_previous_login_filter(test_login):
            for login in past_success_logins:
                # logging.debug( "Edit distance", editdistance.eval(login["UserName"], test_login.get_user())
                # logging.debug( "Opposite", editdistance.eval(test_login.get_user(), login["UserName"])
                # logging.debug( test_login.get_user(), login["UserName"]
                # logging.debug( test_login.get_client() == login["ClientIp"]
                # logging.debug( editdistance.eval(login["UserName"], test_login.get_user()) == 1
                # logging.debug( editdistance.eval(login["UserName"], test_login.get_user()) is 1
                # logging.debug( "For"
                if test_login.get_client() == login["ClientIp"] and \
                                editdistance.eval(login["UserName"], test_login.get_user()) == 1:
                    logging.debug( "Mistype detected"+str(test_login.get_client()))
                    return False
            return True

        for event in epochclone.get_history_events():
            # Filter out the legit users
            event.logins = filter(mistyped_successful_previous_login_filter, event.get_logins())

        return epochclone

    @staticmethod
    def analyze_coordination_glue(epoch):
        """Analyzes an epoch to extract the coordination element.  This can be the target server/user.  Here we do server"""
        graph = Graph()
        # Nodeset 1
        nodeset1 = set()
        for event in epoch.get_history_events():
            for login in event.get_logins():
                # add the remote host nodeset
                if not login.get_status():
                    # graph.add_vertex(login.get_client())
                    nodeset1.add(login.get_client())

        nodeset2 = set()
        # Nodeset 2
        for event in epoch.get_history_events():
            for login in event.get_logins():
                # add the host nodeset
                if not login.get_status():
                    # graph.add_vertex(login.get_host())
                    nodeset2.add(login.get_host()+"-host")

        # Join Remotehost->localhost
        for vertex in nodeset1.union(nodeset2):
            graph.add_vertex(vertex)
        logging.debug(str(graph))
        # logging.debug( "Vertices", graph.vs["name"]
        for event in epoch.get_history_events():
            for login in event.get_logins():
                if not login.get_status():
                    remotehost = login.get_client()
                    localhost = login.get_host()+"-host"
                    # edge = (remotehost, localhost)
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
                    graph.add_edge(source - 1, destination - 1)

                    # graph.add_edge(remotehost, localhost)
                    # graph.add_edges(edge)

        clusters = graph.clusters()
        # Get the targets(centers of clusters with largest edge count)
        # ("servername",["attackerlist"])
        hostname = ""
        attackers = []
        reslist = []
        for cluster in clusters:
            logging.debug(str(cluster))
            for vertex in cluster:
                degree = graph.degree(vertex)
                if "-host" in graph.vs["name"][vertex]:
                    hostname = graph.vs["name"][vertex]
                else:
                    attackers.append(graph.vs["name"][vertex])
            pair = (hostname,attackers)
            reslist.append(pair)
            hostname = ""
            attackers = []

        # logging.debug( top

        # logging.debug( clusters
        return reslist

    def process(self, epoch):
        """Takes an epoch to determine if singleton/distributed"""
        self.current_epoch = epoch
        # First check singleton
        logging.debug("Filtering out legitimate activity")
        newepoch = self.analyze_past_history(epoch)
        logging.debug("Done" + str(newepoch))

        result = self.check_singleton(newepoch)
        logging.debug("Result"+str(result))
        if result is not None:
            # process singleton
            msg = {"type": "Singleton", "data": result}
            logging.debug(str(msg))
            notifications.notify_both(msg)
            # Block
            ipstoblock = list(set(result[2]))
            for ip in ipstoblock:
                ip = ip.replace("-host","")
                logging.debug("Blocking the ip:"+ip)
                ip_tools.notifyblock(ip,result[0])
            dr.store_result("PROTOCOL_ATTACK", time.strftime("%b %d %H:%M:%S"), "SINGLETON", "SINGLETON_IP:" + str(result[0]))
        else:
            # Then check distributed

            logging.debug( "Analyzing coordination glue")
            hitpair = self.analyze_coordination_glue(newepoch)
            logging.debug( "Is distributed!!!")
            msg = {"type":"Distributed","data":hitpair}
            logging.debug(str(msg))
            # Block ips
            for cluster in hitpair:
                ipstoblock = list(set(cluster[1]))
                for ip in ipstoblock:
                    logging.debug("bLOCKING AT HOST"+cluster[0].replace("-host","")+"THE Ip "+ip)
                    ip_tools.notifyblock(cluster[0].replace("-host",""), ip)
            notifications.notify_both(msg)
            logging.debug(str(hitpair))
            dr.store_result("PROTOCOL_ATTACK", time.strftime("%b %d %H:%M:%S"), "DISTRIBUTED", hitpair)
