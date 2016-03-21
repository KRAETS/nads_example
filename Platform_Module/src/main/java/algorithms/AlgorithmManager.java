package algorithms;

import interfaces.Manager;
import parsing.AlgorithmsOptions;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by pedro on 3/17/16.
 */
public class AlgorithmManager extends Manager {
    private List<Algorithm> algorithmsList;
    private AlgorithmsOptions algOpts;


    public boolean start(String algorithmName){
        for(Algorithm alg:algorithmsList){
            this.getLogger().log(Level.INFO,"Starting:"+alg.getName());
            if(alg.getName().equals(algorithmName)){
                alg.run();
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
        return false;
    }

    public boolean start(int algorithmNumber){
        if(algorithmNumber<0||algorithmNumber>=algorithmsList.size()){
            this.getLogger().log(Level.SEVERE, "Out of range");
            return false;
        }
        algorithmsList.get(algorithmNumber).run();
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
            alg.run();
        }
    }
    public void stopAll(){
        for(Algorithm alg:algorithmsList){
            alg.stop();
        }
    }
    public AlgorithmManager(AlgorithmsOptions algOpts){
        this.algorithmsList = new LinkedList<Algorithm>();
        this.algOpts = algOpts;
        this.configure();
    }

}
