package dataretrieval;

import interfaces.Manager;
import parsing.DataRetrievalOptions;

/**
 * Created by pedro on 3/17/16.
 */
public class DataRetrievalManager extends Manager {
    private DataRetrievalOptions datRetOpts;
    public DataRetrievalManager(DataRetrievalOptions dataRetrievalOptions) {
        this.datRetOpts = dataRetrievalOptions;
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
