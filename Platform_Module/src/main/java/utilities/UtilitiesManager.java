package utilities;

import interfaces.Manager;
import org.apache.commons.io.FileUtils;
import parsing.UtilitiesOptions;

import java.io.File;
import java.io.IOException;
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
        //Restart the service
        try{
            Runtime.getRuntime().exec("sudo service logstash restart");
        }
        catch(Exception e){
            this.getLogger().log(Level.SEVERE,"Problem restarting the logstash service"+e.toString());
            restoreBackup();
            return false;
        }

        //Test the service
        try{
            Process p = Runtime.getRuntime().exec("sudo service logstash status");
            p.waitFor();
            String result = p.getOutputStream().toString();
            if(result.contains("Exited")||result.contains("Error")){
                return false;
            }
            return true;
        }
        catch(Exception e){
            this.getLogger().log(Level.SEVERE,"Problem restarting the logstash service"+e.toString());
            restoreBackup();
            return false;
        }
        //if not restore backup and restart the service
    }

    private void restoreBackup() {
        try {
            FileUtils.copyFile(new File(utilOpts.getOriginalPatternFileLocation()+".bak"), new File(utilOpts.getOriginalPatternFileLocation()));
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE,"Problem restoring backup..."+e.toString());
            e.printStackTrace();
        }

    }
}
