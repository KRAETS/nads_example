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
public class PlatformManager extends Manager {
    private String configFile;

    /**
     * Gets parser.
     *
     * @return the parser
     */
    public Parser getParser() {
        return parser;
    }

    private Parser parser;
    private DataRetrievalManager dataRetMan;
    private AlgorithmManager algMan;
    private NotificationManager notMan;
    private VisualizationManager visMan;
    private UtilitiesManager utilMan;
    private String defaultLogLocation = "nadsplatform.log";

    /**
     * Instantiates a new Platform manager.
     *
     * @param options the options
     */
    public PlatformManager(String[] options){

    }

    /**
     * Instantiates a new Platform manager.
     *
     * @param configFile  the config file
     * @param logLocation the log location
     */
    public PlatformManager(String configFile, String logLocation) {
        this.configFile = configFile;
        parser = new Parser(this.configFile);
        if(logLocation!=null)
            setupLogger(logLocation);
        else
            setupLogger(defaultLogLocation);
        this.configure();
    }

    /**
     * Method to configure logging to a file
     * @param logLocation log file location
     */
    private void setupLogger(String logLocation) {
        setLogger(Logger.getLogger("NadsLogger"));
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler(logLocation);
            this.getLogger().addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            // the following statement is used to log any messages
            this.getLogger().info("Logger started");

        } catch (SecurityException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    @Override
    public boolean start() {
        boolean operationSuccessful = false;
        //Try to extract the options after validating them
        operationSuccessful = parser.extractOptions();
        if(!operationSuccessful){
            this.getLogger().log(Level.SEVERE,"Could not extract options. Exiting...");
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
            this.getLogger().log(Level.INFO,"Worked");
        }
        catch (Exception e){
            this.getLogger().log(Level.SEVERE,"Failed to initialize modules:"+e.toString());
            this.getLogger().log(Level.SEVERE,e.toString());
            System.exit(5);
        }

        return true;
    }

    /**
     * Method to handle the initialization of the platform manager.  Sets up the logging logic.
     * @param platformOptions custom options for the manager
     */
    private void initPlatformManager(PlatformOptions platformOptions) {
        if(platformOptions.getLogLocation()!=null){
            this.getLogger().log(Level.INFO,"Changing the log location to:"+platformOptions.getLogLocation());
            setupLogger(platformOptions.getLogLocation());
        }
    }

    /**
     * Method to handle the initialization of the algorithms manager. Starts the manager
     * @param algorithmsOptions custom options for the manager
     */
    private void initAlgorithmsManager(AlgorithmsOptions algorithmsOptions) {
        this.algMan = new AlgorithmManager(algorithmsOptions,this.getLogger());
        this.algMan.start();
    }
    /**
     * Method to handle the initialization of the visualization manager. Starts the manager
     * @param visualizationOptions custom options for the manager
     */
    private void initVisualizationManager(VisualizationOptions visualizationOptions) {
        this.visMan = new VisualizationManager(visualizationOptions,this.getLogger());
        this.visMan.start();
    }
    /**
     * Method to handle the initialization of the data retrieval manager. Starts the manager
     * @param dataRetrievalOptions custom options for the manager
     */
    private void initDataRetrievalManager(DataRetrievalOptions dataRetrievalOptions) {
        this.dataRetMan = new DataRetrievalManager(dataRetrievalOptions,this.getLogger());
        this.dataRetMan.start();
    }

    private void initUtilitiesManager(UtilitiesOptions utilitiesOptions) {
        this.utilMan = new UtilitiesManager(utilitiesOptions,this.getLogger());
        this.utilMan.start();
    }

    private void initNotificationManager(NotificationOptions notificationOptions) {
        Option algorithmNames = new Option("validalgorithmslist", Option.OptionType.STRINGLIST);
        algorithmNames.setOptionStringList(this.getParser().getAlgorithmsOptions().getAlgorithmNames());
        notificationOptions.addOption("validalgorithmslist",algorithmNames);

        this.notMan = new NotificationManager(notificationOptions,this.getLogger());
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
            this.getLogger().log(Level.SEVERE,"Problem shutting down:"+e.toString());
            System.exit(6);
        }
        return true;
    }

    public boolean configure() {
        return true;
    }

}
