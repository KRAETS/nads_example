package parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pedro on 3/18/16.
 */
public class AlgorithmsOptions extends Options {
    public AlgorithmsOptions(){}
 
    public List<String> getAlgorithmsname(){
        return new ArrayList<String>(this.optionMap.keySet());
    }
    
    public Map<String, String> getAlgorithmParamether(String alg){
        return this.getOpt(alg).getOptionMap();
    }
}
