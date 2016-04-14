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


    /**
     * Start a specific algorithm by name.
     *
     * @param algorithmName the algorithm name
     * @return boolean return status
     */
    public boolean start(String algorithmName){
        for(Algorithm alg:algorithmsList){
            if(alg.getName().equals(algorithmName)){
                alg.start();
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

    /**
     * stops all the algorithms
     * @return boolean stop status
     */
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


    /**
     * configures the manager by creating all the algorithms in the config file and passing their parameters
     * @return boolean configuration status
     */
    public boolean configure() {
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

    /**
     * Starts a specific algorithm according to the order it is in the list
     *
     * @param algorithmNumber the algorithm number
     * @return boolean status of the starting
     */

    public boolean start(int algorithmNumber){
        if(algorithmNumber<0||algorithmNumber>=algorithmsList.size()){
            this.getLogger().log(Level.SEVERE, "Out of range");
            return false;
        }
        algorithmsList.get(algorithmNumber).start();
        return true;
    }

    /**
     * Stops a specific algorithm by name
     *
     * @param algorithmName the algorithm name
     * @return boolean status of stop
     */

    public boolean stop(String algorithmName){
        for(Algorithm alg:algorithmsList){
            if(alg.getName().equals(algorithmName)){
                alg.stop();
                return true;
            }
        }
        return false;
    }

    /**
     * Stops a specific algorithm by the number in which it is specified in the configuration file
     *
     * @param algorithmNumber the algorithm number
     * @return the boolean
     */

    public boolean stop(int algorithmNumber){
        if(algorithmNumber<0||algorithmNumber>=algorithmsList.size()){
            return false;
        }
        algorithmsList.get(algorithmNumber).stop();
        return true;
    }

    /**
     * Start all algorithms.
     */

    public void startAll(){
        for(Algorithm alg:algorithmsList){
            alg.start();
        }
    }

    /**
     * Stop all algorithms.
     */
    public void stopAll(){
        for(Algorithm alg:algorithmsList){
            alg.stop();
        }
    }

    /**
     * Instantiates a new Algorithm manager.
     *
     * @param algOpts the algorithm manager opts
     * @param logger  the logger
     */
    public AlgorithmManager(AlgorithmsOptions algOpts, Logger logger){
        this.algorithmsList = new LinkedList<>();
        this.algOpts = algOpts;
        this.setLogger(logger);
        this.configure();
    }

}
