package platform;

import algorithms.AlgorithmManager;
import dataretrieval.DataRetrievalManager;
import interfaces.Manager;
import notifications.NotificationManager;
import parsing.*;
import utilities.UtilitiesManager;
import visualization.VisualizationManager;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by pedro on 3/17/16.
 */
public class PlatformManager implements Manager {
    private String configFile;
    private Parser parser;
    private DataRetrievalManager dataRetMan;
    private AlgorithmManager algMan;
    private NotificationManager notMan;
    private VisualizationManager visMan;
    private UtilitiesManager utilMan;
    private Logger logMan;
    private String defaultLogLocation = "nadsplatform.log";
    public PlatformManager(String[] options){

    }
    public PlatformManager(String configFile, String logLocation) {
        this.configFile = configFile;
        parser = new Parser(this.configFile);
        if(logLocation!=null)
            setupLogger(logLocation);
        else
            setupLogger(defaultLogLocation);
    }

    private void setupLogger(String logLocation) {
        logMan = Logger.getLogger("NadsLogger");
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler(logLocation);
            logMan.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            // the following statement is used to log any messages
            logMan.info("Logger started");

        } catch (SecurityException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }


    public boolean start() {
        boolean operationSuccessful = false;
        //Try to validate the options in the configuration file and look for common problems
        operationSuccessful = parser.validateOptions();
        if(!operationSuccessful) {
            logMan.log(Level.SEVERE,"Could not validate options. Exiting...");
            System.exit(3);
        }
        //Try to extract the options after validating them
        operationSuccessful = parser.extractOptions();
        if(!operationSuccessful){
            logMan.log(Level.SEVERE,"Could not extract options. Exiting...");
            System.exit(4);
        }

        //Start initialization strategy
        try {
            initPlatformManager(parser.getPlatformOptions());
            initNotificationManager(parser.getNotificationOptions());
            initUtilitiesManager(parser.getUtilitiesOptions());
            initDataRetrievalManager(parser.getDataRetrievalOptions());
            initVisualizationManager(parser.getVisualizationOptions());
            initAlgorithmsManager(parser.getAlgorithmsOptions());
        }
        catch (Exception e){
            logMan.log(Level.SEVERE,"Failed to initialize modules:"+e.toString());
            System.exit(5);
        }
        return true;
    }

    private void initPlatformManager(PlatformOptions platformOptions) {
        if(platformOptions.getLogLocation()!=null){
            logMan.log(Level.INFO,"Changing the log location to:"+platformOptions.getLogLocation());
            setupLogger(platformOptions.getLogLocation());
        }
    }

    private void initAlgorithmsManager(AlgorithmsOptions algorithmsOptions) {
        this.algMan = new AlgorithmManager();
        this.algMan.configure(algorithmsOptions);
        this.algMan.start();
    }

    private void initVisualizationManager(VisualizationOptions visualizationOptions) {
        this.visMan = new VisualizationManager();
        this.visMan.configure(visualizationOptions);
        this.visMan.start();
    }

    private void initDataRetrievalManager(DataRetrievalOptions dataRetrievalOptions) {
        this.dataRetMan = new DataRetrievalManager();
        this.dataRetMan.configure(dataRetrievalOptions);
        this.dataRetMan.start();
    }

    private void initUtilitiesManager(UtilitiesOptions utilitiesOptions) {
        this.utilMan = new UtilitiesManager();
        this.utilMan.configure(utilitiesOptions);
        this.utilMan.start();
    }

    private void initNotificationManager(NotificationOptions notificationOptions) {
        this.notMan = new NotificationManager();
        this.notMan.configure(notificationOptions);
        this.notMan.start();
    }

    public boolean stop() {
        try {
            algMan.stop();
            dataRetMan.stop();
            notMan.stop();
            visMan.stop();
            utilMan.stop();
        }
        catch (Exception e){
            logMan.log(Level.SEVERE,"Problem shutting down:"+e.toString());
            System.exit(6);
        }
        return true;
    }

    public boolean configure(Options opts) {
        return false;
    }
}
