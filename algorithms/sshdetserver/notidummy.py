def notify_both(message):
    if message == "Distributed":
        f = open("singletonresults.txt","w+")
        f.write("yes")
        f.close()
    elif message == "Singleton":
        f = open("distributedresults.txt", "w+")
        f.write("yes")
        f.close()
    else:
        print "notifying!!", message

