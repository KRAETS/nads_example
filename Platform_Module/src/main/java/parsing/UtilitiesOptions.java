package parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pedro on 3/18/16.
 */
public class UtilitiesOptions extends Options{
    public UtilitiesOptions(){}
    
    public String getFormattingFile() {
        return this.getOpt("formattingparameterspath").getOptionStringIndividual();
    }
}
