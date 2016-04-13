package parsing;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dude on 3/17/16.
 */
public class Options {

    /**
     * The Option map.
     */
    protected Map<String, Option> optionMap = new HashMap<String, Option>();

    /**
     * Gets opt.
     *
     * @param key the key for the option wanted
     * @return the opt matching the key
     */
    public Option getOpt(String key) {
        return this.optionMap.get(key);
    }

    /**
     * Add option.
     *
     * @param key the key
     * @param opt the option
     */
    public void addOption (String key, Option opt) {
        this.optionMap.put(key,opt);
    }

    /**
     * Remove option boolean.
     *
     * @param key the key of the option to be deleted
     * @return the boolean stating the status of the deletion. True for success, false for failure
     */
    public boolean removeOption(String key)
    {
        try {
            this.optionMap.remove(key);
        }
        catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * Instantiates a new Options.
     */
    public Options (){}
}


