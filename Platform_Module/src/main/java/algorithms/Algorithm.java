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
    private Process algorithmProcess;
    private String commandString, param;
    private boolean exception = true;
    public String getName(){
        return this.name;
    }
    public void run()
    {
        while (true)
        {
            try
            {
                //ProcessBuilder pb = new ProcessBuilder("python", commandString, param);
                ProcessBuilder pb = new ProcessBuilder("python","/home/dude/Documents/capstone/project/nads/algorithms/loop_detec/test.py", "dude", "wut");
                Process algorithmProcess = pb.start();
                BufferedReader in = new BufferedReader(new InputStreamReader(algorithmProcess.getInputStream()));
                while (exception)
                {
                    try {
                        algorithmProcess.exitValue();
                        System.out.println(algorithmProcess.exitValue());
                        System.out.println(in.readLine());
                        System.out.println(in.readLine());
                        exception = false;
                    } catch (IllegalThreadStateException a) {
                        exception = true;
                    }
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public Algorithm(AlgorithmsOptions algorithmOptions){
        this.managerThread = new Thread(this);
    }

    public void start(){
        this.managerThread.run();
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
