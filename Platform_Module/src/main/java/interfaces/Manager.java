package interfaces;

import parsing.Options;

/**
 * Created by pedro on 3/17/16.
 */
public interface Manager {
    /**
     * Method that starts a manager
     * @return Status of startup operation
     */
    boolean start();

    /**
     * Method that sends a stop signal to a manager
     * @return Status for stop operation
     */
    boolean stop();

    /**
     * Method that initializes a manager given a set of options
     * @param opts Options for the manager.  Will be one of the Options classes
     * @return Status for configuration
     */
    boolean configure(Options opts);
}
