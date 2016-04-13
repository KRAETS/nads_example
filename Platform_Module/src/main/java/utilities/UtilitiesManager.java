package utilities;

import interfaces.Manager;
import org.apache.commons.io.FileUtils;
import parsing.UtilitiesOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by pedro on 3/18/16.
 */
public class UtilitiesManager extends Manager {
    private UtilitiesOptions utilOpts;

    /**
     * Instantiates a new Utilities manager.
     *
     * @param utilitiesOptions the utilities options
     * @param logger           the logger
     */
    public UtilitiesManager(UtilitiesOptions utilitiesOptions, Logger logger) {
        this.utilOpts = utilitiesOptions;
        this.setLogger(logger);
        this.configure();
    }

    public boolean start() {
        try {
            this.addConfiguration();
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
        if(this.utilOpts.getNewPatternFileLocation()==null){
            this.getLogger().log(Level.SEVERE, "No formatting file");
            return false;
        }
        if(this.utilOpts.getOriginalPatternFileLocation()==null){
            this.getLogger().log(Level.SEVERE, "No path to original file");
            return false;
        }
        return true;
    }

    /**
     * Add configuration file to the currently running logstash.
     * @return boolean return status of adding the configuration.  False if not root when executing
     */
    public boolean addConfiguration(){
        if(!this.configure()){
            this.getLogger().log(Level.SEVERE,"Missing parameters for configuration swap.");
            return false;
        }
        //Copy the other file as a backup
        try {
            FileUtils.copyFile(new File(utilOpts.getOriginalPatternFileLocation()), new File(utilOpts.getOriginalPatternFileLocation() + ".bak"));
        }
        catch (Exception e){
            this.getLogger().log(Level.SEVERE,"Problem making a backup:"+e.toString());
            return false;
        }
        //Write the new file as the new configuration
        try {
            FileUtils.copyFile(new File(utilOpts.getNewPatternFileLocation()), new File(utilOpts.getOriginalPatternFileLocation()));
        }
        catch (Exception e){
            this.getLogger().log(Level.SEVERE,"Problem copying the new formatting scheme:"+e.toString());
            restoreBackup();
            return false;
        }
        //Test the configuration file
        try{

            String expected = "Configuration OK";
            Process p = Runtime.getRuntime().exec("service logstash configtest");
            p.waitFor();
            String result = "";
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (true){
                String current = input.readLine();
                if(current == null){
                    break;
                }
                result += current;
            }
            if(!result.contains(expected)){
                throw new java.text.ParseException("Did not pass configtest check",0);
            }
        }
        catch (Exception e){
            this.getLogger().log(Level.SEVERE,"Could not pass configuration test... Resetting log format");;
            restoreBackup();
            return false;
        }
        
        //Restart the service
        try{
            Runtime.getRuntime().exec("service logstash restart");
        }
        catch(Exception e){
            this.getLogger().log(Level.SEVERE,"Problem restarting the logstash service"+e.toString());
            restoreBackup();
            return false;
        }

        //Test the service
        try{
            Process p = Runtime.getRuntime().exec("service logstash status");
            p.waitFor();
            String result = "";
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (true){
                String current = input.readLine();
                if(current == null){
                    break;
                }
                result += current;
            }

            input.close();
            return true;
        }
        catch(Exception e){
            this.getLogger().log(Level.SEVERE,"Problem restarting the logstash service"+e.toString());
            //if not restore backup and restart the service
            restoreBackup();
            return false;
        }
    }

    /**
     * Method to restore the original logstash configuration back if replacement was not possible
     * prints an exception if not root!
     */
    private void restoreBackup() {
        try {
            FileUtils.copyFile(new File(utilOpts.getOriginalPatternFileLocation()+".bak"), new File(utilOpts.getOriginalPatternFileLocation()));
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE,"Problem restoring backup..."+e.toString());
            e.printStackTrace();
        }

    }
}
