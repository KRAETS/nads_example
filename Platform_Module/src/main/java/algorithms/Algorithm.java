package algorithms;

import parsing.Options;

import java.io.IOException;

/**
 * Created by pedro on 3/20/16.
 */
public class Algorithm implements Runnable {
    private String name;
    private Thread managerThread;
    private Process algorithmProcess;
    private String commandString;
    public String getName(){
        return this.name;
    }
    public void run() {
        try {
            algorithmProcess = Runtime.getRuntime().exec(commandString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Algorithm(Options algorithmOptions){
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
