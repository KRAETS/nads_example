package parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pedro on 3/18/16.
 */
public class AlgorithmsOptions extends Options {
    /**
     * Instantiates a new Algorithms options.
     */
    public AlgorithmsOptions(){}

    /**
     * Get algorithm names list.
     *
     * @return List<String> containing algorithm names
     */
    public List<String> getAlgorithmNames(){
        return new ArrayList<String>(this.optionMap.keySet());
    }

    /**
     * Get algorithm parameters map.
     *
     * @param String alg The algorithm name you want to look for
     * @return Map<String, String> containing all params for an algorithms
     */
    public Map<String, String> getAlgorithmParameters(String alg){
        return this.getOpt(alg).getOptionMap();
    }


    /**
     * Get the folder cotaining the algorithm.
     *
     * @param alg the algorithm name to be searched
     * @return String with the algorithm location
     */
    public String getAlgorithmFolder(String alg){
        return this.getOpt(alg).getOptionMap().get("folder");
    }
}
