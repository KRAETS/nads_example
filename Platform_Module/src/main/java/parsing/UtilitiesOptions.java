package parsing;

/**
 * Created by pedro on 3/18/16.
 */
public class UtilitiesOptions extends Options{
    public UtilitiesOptions(){}
    
    public String getPath(){
        return null;
    }
    
    public boolean setPath(){
        return true;
    }

    public String getOriginalPatternFileLocation() {
        String opfl = this.getOpt("originalpatternfilelocation").getOptionStringIndividual();
        return opfl;
    }

    public String getNewPatternFileLocation() {
        String npfl = this.getOpt("newpatternfilelocation").getOptionStringIndividual();
        return npfl;
    }
}
