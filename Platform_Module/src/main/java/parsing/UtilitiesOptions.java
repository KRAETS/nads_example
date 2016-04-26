package parsing;

/**
 * Created by pedro on 3/18/16.
 */
public class UtilitiesOptions extends Options{
    /**
     * Instantiates a new Utilities options.
     */
    public UtilitiesOptions(){}

    /**
     * Gets original pattern file location.
     *
     * @return the string with the original pattern file location
     */
    public String getOriginalPatternFileLocation() {
        return this.getOpt("originalpatternfilelocation").getOptionStringIndividual();
    }

    /**
     * Gets new pattern file location.
     *
     * @return the string with the new pattern file location
     */
    public String getNewPatternFileLocation() {
        return this.getOpt("newpatternfilelocation").getOptionStringIndividual();
    }
}
