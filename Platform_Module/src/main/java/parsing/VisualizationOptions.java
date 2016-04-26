package parsing;

/**
 * Created by pedro on 3/18/16.
 */
public class VisualizationOptions extends Options {
    /**
     * Instantiates a new Visualization options.
     */
    public VisualizationOptions(){}

    //public Map<String, Option> getVisualizationParameters(){ return this.optionMap; }

    /**
     * Get kibana template folder string.
     *
     * @return the string with the location for the kibana template folder
     */
    public String getKibanaTemplateFolder(){
        return this.getOpt("templatefolder").getOptionStringIndividual();

    }

    /**
     * Get kibana port string.
     *
     * @return the string with the kibana port number
     */
    public String getKibanaPort(){
        return this.getOpt("kibanaport").getOptionStringIndividual();
    }

    /**
     * Get kibana address string.
     *
     * @return the string with the kibana ip address
     */
    public String getKibanaAddress(){
        return this.getOpt("kibanaaddress").getOptionStringIndividual();
    }
}
