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
     * @return the list
     */
    public List<String> getAlgorithmNames(){
        return new ArrayList<String>(this.optionMap.keySet());
    }

    /**
     * Get algorithm parameters map.
     *
     * @param alg the alg
     * @return the map
     */
    public Map<String, String> getAlgorithmParameters(String alg){
        return this.getOpt(alg).getOptionMap();
    }

    /**
     * Get algorithm specifics string.
     *
     * @param alg the alg
     * @return the string
     */
    public String getAlgorithmSpecifics(String alg){
        return this.getOpt(alg).getOptionMap().get("notifiablealgorithms");
    }

    /**
     * Get example id string.
     *
     * @param alg the alg
     * @return the string
     */
    public String getExampleID(String alg){
        return this.getOpt(alg).getOptionMap().get("exampleip");
    }

    /**
     * Get algorithm model string.
     *
     * @param alg the alg
     * @return the string
     */
    public String getAlgorithmModel(String alg){
        return this.getOpt(alg).getOptionMap().get("model");
    }

    /**
     * Get algorithm trap oid string.
     *
     * @param alg the alg
     * @return the string
     */
    public String getAlgorithmTrapOID(String alg){
        return this.getOpt(alg).getOptionMap().get("trap_oid");
    }

    /**
     * Get algorithm folder string.
     *
     * @param alg the alg
     * @return the string
     */
    public String getAlgorithmFolder(String alg){
        return this.getOpt(alg).getOptionMap().get("folder");
    }
}
