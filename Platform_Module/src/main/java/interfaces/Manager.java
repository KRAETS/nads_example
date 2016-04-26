package interfaces;

import java.util.logging.Logger;

/**
 * Created by pedro on 3/17/16.
 */
public abstract class Manager {
    private Logger logMan;

    /**
     * Method that starts a manager
     *
     * @return Status of startup operation
     */
    public abstract boolean start();

    /**
     * Method that sends a stop signal to a manager
     *
     * @return Status for stop operation
     */
    public abstract boolean stop();

    /**
     * Method that initializes a manager given a set of options
     *
     * @return Status for configuration
     */
    public abstract boolean configure();

    /**
     * Method that sets a logger for the class.  Checks if the logger is null
     *
     * @param logger the logger
     * @return the boolean
     */
    public boolean setLogger(Logger logger){
        try{
            if(logger==null)
                throw new NullPointerException("Null logger");
            this.logMan = logger;
            return true;
        }
        catch(Exception e){
            System.err.println(e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get logger logger.
     *
     * @return the logger
     */
    public Logger getLogger(){
        return this.logMan;
    }
}
