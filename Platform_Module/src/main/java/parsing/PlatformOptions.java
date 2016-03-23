package parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pedro on 3/18/16.
 */
public class PlatformOptions extends Options {
    public PlatformOptions(){}
    
    public String getLogLocation() {
        try{
            return this.getOpt("logtofile").getOptionStringIndividual();
        }
        catch (Exception e){
            System.err.println("Could not find the logtofile option inside the main options");
            return null;
        }
    }

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
