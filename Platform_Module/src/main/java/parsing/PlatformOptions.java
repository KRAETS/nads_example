package parsing;

/**
 * Created by pedro on 3/18/16.
 */
public class PlatformOptions extends Options {
    /**
     * Instantiates a new Platform options.
     */
    public PlatformOptions(){}

    /**
     * Gets log location.
     *
     * @return the string with the log location
     */
    public String getLogLocation() {
        try{
            return this.getOpt("logtofile").getOptionStringIndividual();
        }
        catch (Exception e){
            System.err.println("Could not find the logtofile option inside the main options");
            return null;
        }
    }

    /**
     * Gets working directory.
     *
     * @return the string with the working directory
     */
    public String getWorkingDirectory()
    {
        try
        {
            return this.getOpt("workingdirectory").getOptionStringIndividual();
        }catch (Exception e)
        {
            System.err.println("Could not find the logtofile option inside the main options");
            return null;
        }
    }
}
