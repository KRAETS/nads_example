package parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pedro on 3/18/16.
 */
public class DataRetrievalOptions extends Options {
    //private Map<String, String> parameters = this.getOpt("").getOptionMap();

    public DataRetrievalOptions(){}
    
    public Map<String, String> getDataRetrievalParameters(){
        return parameters;
    }
    
    public String getLogstashPort(){
        return this.getOpt("logstashport").getOptionStringIndividual();
    }
    
    public String getLogstashAddress(){
        return parameters("logstashaddress");
    }
    
    public String getElasticSearchPort(){
        return parameters("elasticsearchport");
    }
   
    public String getElasticSearchAddress(){
        return parameters("elasticsearchaddress");
    }
}
