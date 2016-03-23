package parsing;

import java.util.ArrayList;
import java.util.HashMap;
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
    
    public Map<String, String> getAlgorithmParamethers(String alg){
        return this.getOpt(alg).getOptionMap();
    }

    public String getAlgorithmSpecifics(String alg){
        return this.getOpt(alg).getOptionMap().get("notifiablealgorithms");
    }

    public String getExampleID(String alg){
        return this.getOpt(alg).getOptionMap().get("exampleip");
    }

    public String getAlgorithmModel(String alg){
        return this.getOpt(alg).getOptionMap().get("model");
    }

    public String getAlgorithmTrapOID(String alg){
        return this.getOpt(alg).getOptionMap().get("trap_oid");
    }

    public String getAlgorithmFolder(String alg){
        return this.getOpt(alg).getOptionMap().get("folder");
    }
}
