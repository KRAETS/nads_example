package algorithms;

import interfaces.Manager;
import parsing.AlgorithmsOptions;
import parsing.Options;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by pedro on 3/17/16.
 */
public class AlgorithmManager implements Manager {
    private List<Algorithm> algorithmsList;
    private AlgorithmsOptions algOpts;

    public AlgorithmManager(){
        this.algorithmsList = new LinkedList<Algorithm>();

    }
    public boolean start(String algorithmName){
        for(Algorithm alg:algorithmsList){
            if(alg.getName().equals(algorithmName)){
                alg.run();
                return true;
            }
        }
        return false;
    }

    public boolean start() {
        return false;
    }

    public boolean stop() {
        return false;
    }

    public boolean configure(Options opts) {
        return false;
    }

    public boolean start(int algorithmNumber){
        if(algorithmNumber<0||algorithmNumber>=algorithmsList.size()){
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
        this.algOpts = algOpts;
        this.configure(algOpts);
    }

}
