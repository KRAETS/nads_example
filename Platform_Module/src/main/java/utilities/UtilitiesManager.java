package utilities;

import interfaces.Manager;
import parsing.Options;

/**
 * Created by pedro on 3/18/16.
 */
public class UtilitiesManager implements Manager {
    public boolean start() {
        return false;
    }

    public boolean stop() {
        return false;
    }

    public boolean configure(Options opts) {
        return false;
    }
}
