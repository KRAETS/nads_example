package Platform_module;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dude on 3/17/16.
 */
public class Options {

    private Map<String, Option> optionMap = new HashMap<String, Option>();

    public Option getOpt(String key) {
        return this.optionMap.get(key);
    }

    public void addOption (String key, Option opt) {
        this.optionMap.put(key,opt);
    }

    public boolean removeOption(String key)
    {
        this.optionMap.remove(key);
    }
}

