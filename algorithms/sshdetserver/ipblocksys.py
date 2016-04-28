import sys
import requests
from subprocess import check_call
LOG_FILENAME = 'example.log'
import logging

logging.basicConfig(filename=LOG_FILENAME, level=logging.DEBUG)


def block(ip_to_block):
    logging.debug( "Blocking"+str(ip_to_block))
    res = None
    try:
        res = check_call(["iptables", "-A", "INPUT", "-s", ip_to_block, "-j","DROP"])
    except Exception as e:
        logging.debug( "Could not complete call res:"+str(res)+"exception:"+str(e))
    logging.debug( "Done")
    return 0

def unblock(ip_to_unblock):
    logging.debug( "Unblocking"+str(ip_to_unblock))
    res = None
    try:
        res = check_call(["iptables", "-D", "INPUT", "-s", ip_to_unblock, "-j", "DROP"])
    except Exception as e:
        logging.debug( "Could not complete call res:"+str(res)+"exception:"+str(e))
    logging.debug( "Done")
    return 0

def notifyblock(host, ip_to_block):
    try:
        requests.post("http://"+host+":8004/blockip",ip_to_block)
    except Exception as e:
        logging.debug( "Could not notify host of ip to block"+ str(e))
    return
if __name__ == '__main__':
    if len(sys.argv)<3:
        logging.debug( "Not enough arguments")
    else:
        if sys.argv[2] == "block":
            block(sys.argv[1])
        elif sys.argv[2] == "unblock":
            unblock(sys.argv[1])
        else:
            logging.debug( "Arguments must be: IPTOBLOCK block|unblock")