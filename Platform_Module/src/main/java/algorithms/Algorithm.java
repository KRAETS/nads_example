package algorithms;

import com.google.gson.Gson;
import parsing.AlgorithmsOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by pedro on 3/20/16.
 */
public class Algorithm implements Runnable {
    private String name;
    private Logger logMan;
    private long startTime;
    private long recentRestartTime;
    private int restartCount = 0;
    private boolean loop = true;
    private Thread managerThread;
    private long hour = 1000*1000*60;
    private AlgorithmsOptions algOpts;
    private Process algorithmProcess;
    private String commandString, param;
    private boolean exception = true;


    /**
     * Get name string.
     *
     * @return the string
     */
//Setters and getter from thread name
    public String getName(){
        return this.name;
    }

    /**
     * Sets name.
     *
     * @param n the n
     */
    public void setName(String n) {this.name = n;}

    //Code what will be run on the thread. In charge of making sure the algorithms are always running.
    public void run()
    {
        while (loop)
        {
            try
            {

                //ProcessBuilder pb = new ProcessBuilder("python", commandString, param);
                param = this.algOpts.getAlgorithmParameters(this.getName()).toString();
                commandString = "python "+(new Gson()).toJson(this.algOpts.getAlgorithmParameters(this.getName()));
                //Creates new python process with a json of the parameters and starts it
                ProcessBuilder pb = new ProcessBuilder("python",this.algOpts.getAlgorithmFolder(this.getName()),(new Gson()).toJson(this.algOpts.getAlgorithmParameters(this.getName())));
                pb.inheritIO();
                algorithmProcess = pb.start();
                //Creates a reader of the output of the process for when theres an error
                BufferedReader in = new BufferedReader(new InputStreamReader(algorithmProcess.getInputStream()));
                exception = true;
                while (exception)
                {
                    //checks if last failure was more than 6 hours ago to reset count.
                    if (((System.nanoTime()/hour)/(1000*60)) - ((recentRestartTime/hour)/(1000*60)) >= 1)
                    {
                        restartCount =0;
                        recentRestartTime = System.nanoTime();
                    }
                    try {
                        //Checks if the python process ended. If it does prints out the exit code and error message is any and breaks from the checking loop
                        algorithmProcess.exitValue();
                        System.out.println("ExitVal:"+algorithmProcess.exitValue());
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
                            // Puts the thread to sleep for 5 minutes to lessen processor usage
                            Thread.sleep(5000*60);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }
                //When the python process ends updates counter of program fails and array with failure times
                if (restartCount < 5)
                {
                    if (restartCount == 0)
                    {
                        startTime = System.nanoTime();
                    }
                    restartCount++;
                    recentRestartTime = System.nanoTime();
                }
                //If program has failed more than 5 times shut it down till furthere action is taken

                else
                {
                    loop = false;
                    this.getLogger().log(Level.SEVERE, "Could not restart " + this.getName() + " algorithm in thread");
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Instantiates a new Algorithm.
     *
     * @param algorithmOptions the algorithm options
     * @param logger           the logger
     */
    public Algorithm(AlgorithmsOptions algorithmOptions, Logger logger)
    {
        this.algOpts = algorithmOptions;
        this.setLogger(logger);
    }

    /**
     * Start.
     */
//Starts the thread and creates a new one if none exists
    public void start()
    {
        if (this.managerThread == null)
        {
            this.managerThread = new Thread(this, this.name);
        }
        this.managerThread.start();
    }

    /**
     * Stop boolean.
     *
     * @return the boolean
     */
//Kills the current thread
    public boolean stop()
    {
        try
        {
            this.managerThread.interrupt();
            return true;
        }catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Interrupt.
     */
    public void interrupt(){
        algorithmProcess.destroy();
    }

    /**
     * Sleep.
     */
    public void sleep(){
        //TODO Implement
    }

    /**
     * Set logger boolean.
     *
     * @param logger the logger
     * @return the boolean
     */
//Setter and getters for the logger
    public boolean setLogger(Logger logger){
        try{
            if(logger==null)
                throw new NullPointerException("Null logger");
            this.logMan = logger;
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
        return this.logMan;
    }
}
