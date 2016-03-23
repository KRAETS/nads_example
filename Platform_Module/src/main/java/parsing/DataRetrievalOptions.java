package parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pedro on 3/18/16.
 */
public class DataRetrievalOptions extends Options {
    public DataRetrievalOptions(){}
    
    public Map<String, String> getDataRetrievalParameters(){
        Map<String, String> map = null;
        for (Map.Entry<String, Option> entry : this.optionMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getOptionStringIndividual());
        }
        return map;
    }
    
    public String getLogstashPort(){
        return this.getOpt("logstashport").getOptionStringIndividual();
    }
    
    public String getLogstashAddress(){
        return this.getOpt("logstashaddress").getOptionStringIndividual();
    }
    
    public String getElasticSearchPort(){
        return this.getOpt("elasticsearchport").getOptionStringIndividual();
    }
   
    public String getElasticSearchAddress(){
        return this.getOpt("elasticsearchaddress").getOptionStringIndividual();
    }
}
