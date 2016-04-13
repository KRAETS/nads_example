package notifications;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import parsing.NotificationOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    /**
     * Get name string.
     *
     * @return the string
     */
    public String getName(){
        return this.name;
    }

    /**
     * Instantiates a new Notification.
     *
     * @param notificationOptions the notification options
     * @param logger              the logger
     */
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
                userinfo= gson.toJson(t);

                //Add the valid algorithms
                JsonObject jsonElement = new JsonObject();
                JsonArray validList = new JsonArray();
                for(String s : notOpts.getValidAlgorithms()){
                    validList.add(s);
                }
                jsonElement.add("list",validList);
                String js = gson.toJson(jsonElement);
                ProcessBuilder pb = new ProcessBuilder("python",this.notOpts.getPath() ,userinfo, gson.toJson(jsonElement), this.notOpts.getEmail(), this.notOpts.getEmailPassword());
                notificationProcess = pb.start();
                BufferedReader in = new BufferedReader(new InputStreamReader(notificationProcess.getInputStream()));
                exception = true;
                this.getLogger().log(Level.INFO,"Starting notification process... If no further output then it is successful");
                while (exception) {
                    if (((System.nanoTime()/hour)/(1000*60)) - ((recentRestartTime/hour)/(1000*60)) >= 1) {
                        restartCount =0;
                        recentRestartTime = System.nanoTime();
                    }
                    try {
                        notificationProcess.exitValue();
                        System.out.println("ExitVal:"+notificationProcess.exitValue());
                        while(true) {
                            String line = in.readLine();
                            if(line == null)
                                break;
                            else
                                System.out.println(line);
                        }
                        exception = false;
                    } catch (IllegalThreadStateException a) {
                    try {
                        Thread.sleep(5000*60);
                        } catch (Exception e) {
                            System.out.println(e);
                        }

                    }
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

    /**
     * Start boolean.
     *
     * @return the boolean
     */
    public boolean start() {
        if (this.managerThread == null)
            this.managerThread = new Thread(this, this.name);

        this.managerThread.start();
        return true;
    }

    /**
     * Stop.
     */
    public void stop() {
        this.managerThread.interrupt();
    }

    /**
     * Interrupt.
     */
    public void interrupt(){
        notificationProcess.destroy();
    }

    /**
     * Sleep.
     */
    public void sleep(){ }

    /**
     * Set logger boolean.
     *
     * @param logger the logger
     * @return the boolean
     */
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

    /**
     * Get logger logger.
     *
     * @return the logger
     */
    public Logger getLogger(){
        return this.notMan;
    }
}
