package parsing;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedro on 3/18/16.
 */
public class DataRetrievalOptions extends Options {
    /**
     * Instantiates a new Data retrieval options.
     */
    public DataRetrievalOptions(){}

    /**
     * Get data retrieval parameters map.
     * @return map, all options for the module
     */
    public Map<String, String> getDataRetrievalParameters(){
        Map<String, String> map = new HashMap<String, String>();
        for (Map.Entry<String, Option> entry : this.optionMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getOptionStringIndividual());
        }
        return map;
    }

    /**
     * Get logstash port string.
     * @return string, logstash port number to use
     */
    public String getLogstashPort(){
        return this.getOpt("logstashport").getOptionStringIndividual();
    }

    /**
     * Get logstash address string.
     * @return string, logstach url
     */
    public String getLogstashAddress(){
        return this.getOpt("logstashaddress").getOptionStringIndividual();
    }

    /**
     * Get elastic search port string.
     * @return string, elastic search port number to use
     */
    public String getElasticSearchPort(){
        return this.getOpt("elasticsearchport").getOptionStringIndividual();
    }

    /**
     * Get elastic search address string.
     * @return string, elastic search url
     */
    public String getElasticSearchAddress(){

        return this.getOpt("elasticsearchaddress").getOptionStringIndividual();
    }
}
