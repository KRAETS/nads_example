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
    public String getName(){
        return this.name;
    }
    public void setName(String n) {this.name = n;}
    public void run()
    {
        while (loop)
        {
            try
            {
                //ProcessBuilder pb = new ProcessBuilder("python", commandString, param);
                param = this.algOpts.getAlgorithmParameters(this.getName()).toString();
                ProcessBuilder pb = new ProcessBuilder("python",this.algOpts.getAlgorithmParameters(this.getName()).get("folder"), "dude", "wut");
                algorithmProcess = pb.start();
                BufferedReader in = new BufferedReader(new InputStreamReader(algorithmProcess.getInputStream()));
                exception = true;
                while (exception)
                {
                    if (((System.nanoTime()/hour)/(1000*60)) - ((recentRestartTime/hour)/(1000*60)) >= 1)
                    {
                        restartCount =0;
                        recentRestartTime = System.nanoTime();
                    }
                    try {
                        algorithmProcess.exitValue();
                        System.out.println(algorithmProcess.exitValue());
                        System.out.println(in.readLine());
                        System.out.println(in.readLine());
                        exception = false;
                    } catch (IllegalThreadStateException a) {
                    }
                }
                if (restartCount < 5)
                {
                    if (restartCount == 0)
                    {
                        startTime = System.nanoTime();
                    }
                    restartCount++;
                    recentRestartTime = System.nanoTime();
                }
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

    public Algorithm(AlgorithmsOptions algorithmOptions, Logger logger)
    {
        this.algOpts = algorithmOptions;
        this.setLogger(logger);
    }
    public void start()
    {
        if (this.managerThread == null)
        {
            this.managerThread = new Thread(this, this.name);
        }
        this.managerThread.start();
    }
    public void stop()
    {
        this.managerThread.interrupt();
    }
    public void interrupt(){
        algorithmProcess.destroy();
    }
    public void sleep(){
        //TODO Implement
    }
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

    public Logger getLogger(){
        return this.logMan;
    }
}
