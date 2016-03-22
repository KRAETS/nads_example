package parsing;

/**
 * Created by pedro on 3/18/16.
 */
public class PlatformOptions extends Options {
    public PlatformOptions(){}
    
    public String getLogLocation() {
        try{
            String location = this.getOpt("logtofile").getOptionStringIndividual();
            return location;
        }
        catch (Exception e){
            System.err.println("Could not find the logtofile option inside the main options");
            return null;
        }
    }
}
