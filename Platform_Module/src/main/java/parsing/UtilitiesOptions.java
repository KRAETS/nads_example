package parsing;

/**
 * Created by pedro on 3/18/16.
 */
public class UtilitiesOptions extends Options{
    public UtilitiesOptions(){}

    public String getOriginalPatternFileLocation() {
        return this.getOpt("originalpatternfilelocation").getOptionStringIndividual();
    }

    public String getNewPatternFileLocation() {
        return this.getOpt("newpatternfilelocation").getOptionStringIndividual();
    }
}
