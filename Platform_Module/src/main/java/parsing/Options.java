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
     * @param key the key
     * @return the opt
     */
    public Option getOpt(String key) {
        return this.optionMap.get(key);
    }

    /**
     * Add option.
     *
     * @param key the key
     * @param opt the opt
     */
    public void addOption (String key, Option opt) {
        this.optionMap.put(key,opt);
    }

    /**
     * Remove option boolean.
     *
     * @param key the key
     * @return the boolean
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


