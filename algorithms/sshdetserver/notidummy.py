def notify_both(message):
    if message == "Singleton":
        f = open("singletonresults.txt","w+")
        f.write("yes")
        f.close()
    elif message == "Distributed":
        f = open("distributedresults.txt", "w+")
        f.write("yes")
        f.close()
    else:
        print "notifying!!", message

