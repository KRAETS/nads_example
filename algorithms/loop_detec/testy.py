import subprocess, os, time
from threading import Timer

para = "[\"136.145.59.152\"]"
testdir = os.getcwd() + "/Main_Script_Prototype.py"
#subprocess.call(testdir, shell = True)
p = subprocess.Popen(['python', testdir, para])
#p = subprocess.call(['python', testdir, para])
time.sleep(60*2)
print "does this work"

print p.pid

hi = Timer(120,p.terminate())
#hi.start()