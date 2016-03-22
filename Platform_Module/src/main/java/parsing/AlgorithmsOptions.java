package parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pedro on 3/18/16.
 */
public class AlgorithmsOptions extends Options {
    public AlgorithmsOptions(){}

    public List<String> getAlgorithmNames(){
        return new ArrayList<String>(this.optionMap.keySet());
    }
    
    public Map<String, String> getAlgorithmParameters(String alg){
        return this.getOpt(alg).getOptionMap();
    }
}
