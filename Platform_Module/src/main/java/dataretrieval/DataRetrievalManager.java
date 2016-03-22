package dataretrieval;

import interfaces.Manager;
import parsing.DataRetrievalOptions;

import java.util.logging.Logger;

/**
 * Created by pedro on 3/17/16.
 */
public class DataRetrievalManager extends Manager {
    private DataRetrievalOptions datRetOpts;
    public DataRetrievalManager(DataRetrievalOptions dataRetrievalOptions, Logger logger) {
        this.datRetOpts = dataRetrievalOptions;
        this.setLogger(logger);
        this.configure();
    }
    @Override
    public boolean start() {
        return false;
    }
    @Override
    public boolean stop() {
        return false;
    }
    @Override
    public boolean configure() {
        return false;
    }
}
