package algorithms;

import parsing.AlgorithmsOptions;
import parsing.Options;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by pedro on 3/20/16.
 */
public class Algorithm implements Runnable {
    private String name;
    private Thread managerThread;
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
        while (true)
        {
            try
            {
                //ProcessBuilder pb = new ProcessBuilder("python", commandString, param);
                param = this.algOpts.getAlgorithmParamether(this.getName()).toString();
                ProcessBuilder pb = new ProcessBuilder("python",this.algOpts.getAlgorithmParamether(this.getName()).get("folder"), "dude", "wut");
                Process algorithmProcess = pb.start();
                BufferedReader in = new BufferedReader(new InputStreamReader(algorithmProcess.getInputStream()));
                exception = true;
                while (exception)
                {
                    try {
                        algorithmProcess.exitValue();
                        System.out.println(algorithmProcess.exitValue());
                        System.out.println(in.readLine());
                        System.out.println(in.readLine());
                        exception = false;
                    } catch (IllegalThreadStateException a) {
                    }
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public Algorithm(AlgorithmsOptions algorithmOptions)
    {
        this.algOpts = algorithmOptions;
    }
    public void start()
    {
        if (this.managerThread == null)
        {
            this.managerThread = new Thread(this, this.name);
        }
        this.managerThread.start();
    }
    public void stop(){
        this.managerThread.interrupt();
    }
    public void interrupt(){
        algorithmProcess.destroy();
    }
    public void sleep(){
        //TODO Implement
    }
}
