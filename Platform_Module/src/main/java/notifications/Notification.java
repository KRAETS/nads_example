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
     * Get class name.
     * @return name, a string with the class name.
     */
    public String getName(){
        return this.name;
    }

    /**
     * Instantiates a new Notification.
     * @param notificationOptions the notification options
     * @param logger              the logger of the class
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
                for (String u : users)
                    t.put(u, this.notOpts.getUserInformation(u));

                userinfo= gson.toJson(t);

                //Add the valid algorithms
                JsonObject jsonElement = new JsonObject();
                JsonArray validList = new JsonArray();
                for(String s : notOpts.getValidAlgorithms())
                    validList.add(s);

                jsonElement.add("List",validList);
                String js = gson.toJson(jsonElement);

                //Creates new python process with a json of the parameters and starts it
                ProcessBuilder pb = new ProcessBuilder("python",this.notOpts.getPath() ,userinfo, gson.toJson(jsonElement), this.notOpts.getEmail(), this.notOpts.getEmailPassword());
                pb.inheritIO();
                notificationProcess = pb.start();

                //Creates a reader of the output of the process for when theres an error
                BufferedReader in = new BufferedReader(new InputStreamReader(notificationProcess.getInputStream()));
                exception = true;
                this.getLogger().log(Level.INFO,"Starting notification process... If no further output then it is successful");
                while (exception) {
                    //checks if last failure was more than 6 hours ago to reset count.
                    if (((System.nanoTime()/hour)/(1000*60)) - ((recentRestartTime/hour)/(1000*60)) >= 1) {
                        restartCount =0;
                        recentRestartTime = System.nanoTime();
                    }
                    try {
                        //Checks if the python process ended. If it does prints out the exit code and error message is any and breaks from the checking loop
                        notificationProcess.exitValue();
                        this.getLogger().log(Level.INFO, "ExitVal:" + notificationProcess.exitValue());
                        while(true) {
                            String line = in.readLine();
                            if(line == null)
                                break;
                            else
                                this.getLogger().log(Level.INFO,line);
                        }
                        exception = false;
                    } catch (IllegalThreadStateException a) {
                        try {
                            // Puts the thread to sleep for 5 minutes to lessen processor usage
                            Thread.sleep(5000 * 60);
                        } catch (Exception e) {
                            this.getLogger().log(Level.SEVERE, e.toString());
                        }
                    }
                }
                if (restartCount < 5) {
                    //When the python process ends updates counter of program fails and array with failure times
                    if (restartCount == 0)
                        startTime = System.nanoTime();

                    restartCount++;
                    recentRestartTime = System.nanoTime();
                }
                else {
                    //If program has failed more than 5 times shut it down till furthere action is taken
                    loop = false;
                    this.getLogger().log(Level.SEVERE, "Could not restart " + this.getName() + " in thread");
                }
            }
            catch (IOException e) {
                loop = false;
                this.getLogger().log(Level.SEVERE,"Problem with notification");
                e.printStackTrace();
            }
        }
    }


    /**
     * Starts thread.
     * @return boolean, if the thread was successfully started.
     */
    public void start() {
        if (this.managerThread == null)
            this.managerThread = new Thread(this, this.name);
        this.managerThread.start();
    }

    /**
     * Stop/kills current thread.
     */
    public boolean interrupt() {
        try {
            //Sends an interrupt signal to the thread so it stops
            this.managerThread.interrupt();
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    /**
     * Interrupt/destroy thread.
     */
    public void stop(){
        try {
            notificationProcess.destroy();
        }
        catch(Exception e){
            System.out.println("COuld not stop notifications"+e.toString());
        }
    }

    /**
     * Set logger fot the notification manager.
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
     * Get logger from the notification manager.
     * @return logger, gets the class logger.
     */
    public Logger getLogger(){
        return this.notMan;
    }
}
