package utilities;

import interfaces.Manager;
import parsing.UtilitiesOptions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by pedro on 3/18/16.
 */
public class UtilitiesManager extends Manager {
    private UtilitiesOptions utilOpts;
    public UtilitiesManager(UtilitiesOptions utilitiesOptions, Logger logger) {
        this.utilOpts = utilitiesOptions;
        this.setLogger(logger);
        this.configure();
    }

    public boolean start() {
        try {
            this.addConfiguration(this.utilOpts.getFormattingFile());
            return true;
        }
        catch (Exception e){
            getLogger().log(Level.SEVERE,"Problem setting the new configuration file for the formatting:"+e.toString());
            return false;
        }
    }

    public boolean stop() {
        //This does not stop anything.  It could stop logstash.
        return true;
    }

    public boolean configure() {
        //Verify that the configuration file contains a formatting configuration
        if(this.utilOpts.getFormattingFile()==null){
            this.getLogger().log(Level.SEVERE, "No formatting file");
            return false;
        }
        return true;
    }

    public void addConfiguration(String configurationFile){
        //Copy the other file as a backup
        //Write the new file as the new configuration
        //Restart the service
        //Test the service
    }
}
