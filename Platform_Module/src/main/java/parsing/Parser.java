package parsing;

import com.google.gson.Gson;
/**
 * Created by dude on 3/17/16.
 */
public class Parser {
    private Gson gson = new Gson();
    private String configFile = "";
    private PlatformOptions platOpts;
    private NotificationOptions notOpts;
    private UtilitiesOptions utilOpts;
    private DataRetrievalOptions dataRetOpts;
    private VisualizationOptions visOpts;
    private AlgorithmsOptions algOpts;

    public Parser(String configFile)
    {
        this.configFile = configFile;
    }

    public boolean validateOptions() {
        return true;
    }

    public boolean extractOptions() {
        return true;
    }

    public PlatformOptions getPlatformOptions() {
        return platOpts;
    }

    public NotificationOptions getNotificationOptions() {
        return notOpts;
    }

    public UtilitiesOptions getUtilitiesOptions() {
        return utilOpts;
    }

    public DataRetrievalOptions getDataRetrievalOptions() {
        return dataRetOpts;
    }

    public VisualizationOptions getVisualizationOptions() {
        return visOpts;
    }

    public AlgorithmsOptions getAlgorithmsOptions() {
        return algOpts;
    }
}
