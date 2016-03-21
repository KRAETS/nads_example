package utilities;

import interfaces.Manager;
import parsing.UtilitiesOptions;

/**
 * Created by pedro on 3/18/16.
 */
public class UtilitiesManager extends Manager {
    private UtilitiesOptions utilOpts;
    public UtilitiesManager(UtilitiesOptions utilitiesOptions) {
        this.utilOpts = utilitiesOptions;
        this.configure();
    }

    public boolean start() {
        return false;
    }

    public boolean stop() {
        return false;
    }

    public boolean configure() {
        return false;
    }
}
