package platform;

import algorithms.AlgorithmManager;
import dataretrieval.DataRetrievalManager;
import interfaces.Manager;
import notifications.NotificationManager;
import parsing.Options;
import parsing.Parser;
import visualization.VisualizationManager;

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
