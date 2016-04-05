package notifications;

import com.google.gson.Gson;
import parsing.NotificationOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

/**
 * Created by marie on 3/23/16.
 */
public class Notification implements Runnable {
    private String name = "Notification";
    private Logger notMan;
    private long startTime;
    private long recentRestartTime;
    private int restartCount = 0;
    private boolean loop = true;
    private Thread managerThread;
    private long hour = 1000*1000*60;
    private NotificationOptions notOpts;
    private Process notificationProcess;
    private boolean exception = true;
    private Gson gson = new Gson();
    private List<String> users = new ArrayList<String>();
    private String userinfo;
    private Map<String,Map<String,String>> t = new HashMap<String,Map<String,String>>();

    public String getName(){
        return this.name;
    }

    public Notification(NotificationOptions notificationOptions, Logger logger) {
        this.notOpts = notificationOptions;
        this.setLogger(logger);
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        while (loop) {
            try {
                users = this.notOpts.getNotificationUsers();
                for (String u : users){
                    t.put(u, this.notOpts.getUserInformation(u));
                }
                userinfo= gson.toJson(t.toString());

                ProcessBuilder pb = new ProcessBuilder("python",this.notOpts.getPath(), userinfo, this.notOpts.getEmail(), this.notOpts.getEmailPassword());
                notificationProcess = pb.start();
                BufferedReader in = new BufferedReader(new InputStreamReader(notificationProcess.getInputStream()));
                exception = true;

                while (exception) {
                    if (((System.nanoTime()/hour)/(1000*60)) - ((recentRestartTime/hour)/(1000*60)) >= 1) {
                        restartCount =0;
                        recentRestartTime = System.nanoTime();
                    }
                    try {
                        notificationProcess.exitValue();
                        System.out.println(notificationProcess.exitValue());
                        System.out.println(in.readLine());
                        System.out.println(in.readLine());
                        exception = false;
                    } catch (IllegalThreadStateException a) { }
                }
                if (restartCount < 5) {
                    if (restartCount == 0)
                        startTime = System.nanoTime();

                    restartCount++;
                    recentRestartTime = System.nanoTime();
                }
                else {
                    loop = false;
                    this.getLogger().log(Level.SEVERE, "Could not restart " + this.getName() + " in thread");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean start() {
        if (this.managerThread == null)
            this.managerThread = new Thread(this, this.name);

        this.managerThread.start();
        return true;
    }

    public void stop() {
        this.managerThread.interrupt();
    }

    public void interrupt(){
        notificationProcess.destroy();
    }

    public void sleep(){ }

    protected boolean setLogger(Logger logger){
        try{
            if(logger==null)
                throw new NullPointerException("Null logger");
            this.notMan = logger;
            return true;
        }
        catch(Exception e){
            System.err.println(e.toString());
            e.printStackTrace();
            return false;
        }
    }

    public Logger getLogger(){
        return this.notMan;
    }
}
