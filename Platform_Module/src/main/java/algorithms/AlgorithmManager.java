package algorithms;

import interfaces.Manager;
import parsing.AlgorithmsOptions;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by pedro on 3/17/16.
 */
public class AlgorithmManager extends Manager {
    private List<Algorithm> algorithmsList;
    private AlgorithmsOptions algOpts;


    public boolean start(String algorithmName){
        for(Algorithm alg:algorithmsList){
            if(alg.getName().equals(algorithmName)){
                alg.run();
                this.getLogger().log(Level.INFO,"Starting:"+alg.getName());
                return true;
            }
        }
        return false;
    }


    /**
     * Default start method, simply start all algorithms.
     * @return status of startup
     */
    public boolean start() {
        try{
            this.startAll();
        }
        catch (Exception e){
            this.getLogger().log(Level.SEVERE,"Could not start all the algorithms:"+e.toString());
            return false;
        }
        return true;
    }

    public boolean stop() {
        try{
            this.stopAll();
        }
        catch (Exception e){
            this.getLogger().log(Level.SEVERE,"Could not stop all the algorithms:"+e.toString());
            return false;
        }
        return true;
    }

    public boolean configure() {

        //TODO Read options and start
        try {
            for (String s : this.algOpts.getAlgorithmNames()) {
                Algorithm a = new Algorithm(algOpts, this.getLogger());
                a.setName(s);
                algorithmsList.add(a);
            }
        }catch (Exception e)
        {
            this.getLogger().log(Level.SEVERE, "Could not set up algorithms in manager: "+e.toString());
            return false;
        }
        return true;
    }

    public boolean start(int algorithmNumber){
        if(algorithmNumber<0||algorithmNumber>=algorithmsList.size()){
            this.getLogger().log(Level.SEVERE, "Out of range");
            return false;
        }
        algorithmsList.get(algorithmNumber).start();
        return true;
    }
    public boolean stop(String algorithmName){
        for(Algorithm alg:algorithmsList){
            if(alg.getName().equals(algorithmName)){
                alg.stop();
                return true;
            }
        }
        return false;
    }

    public boolean stop(int algorithmNumber){
        if(algorithmNumber<0||algorithmNumber>=algorithmsList.size()){
            return false;
        }
        algorithmsList.get(algorithmNumber).stop();
        return true;
    }
    public void startAll(){
        for(Algorithm alg:algorithmsList){
            alg.start();
        }
    }
    public void stopAll(){
        for(Algorithm alg:algorithmsList){
            alg.stop();
        }
    }
    public AlgorithmManager(AlgorithmsOptions algOpts, Logger logger){
        this.algorithmsList = new LinkedList<Algorithm>();
        this.algOpts = algOpts;
        this.setLogger(logger);
        this.configure();
    }

}
