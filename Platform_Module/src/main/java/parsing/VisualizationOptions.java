package parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pedro on 3/18/16.
 */
public class VisualizationOptions extends Options {
    public VisualizationOptions(){}

    public Map<String, Option> getVisualizationParameters(){
        return this.optionMap;
    }

    public String getKibanaTemplateFolder(){
        return this.getOpt("templatefolder").getOptionStringIndividual();

    }
    
    public String getKibanaPort(){
        return this.getOpt("kibanaport").getOptionStringIndividual();
    }
    
    public String getKibanaAddress(){
        return this.getOpt("kibanaaddress").getOptionStringIndividual();
    }
}
