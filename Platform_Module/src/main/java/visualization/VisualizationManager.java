package visualization;

import interfaces.Manager;
import parsing.VisualizationOptions;

import java.util.logging.Logger;

/**
 * Created by pedro on 3/17/16.
 */
public class VisualizationManager extends Manager {
    private VisualizationOptions visOpts;

    /**
     * Instantiates a new Visualization manager.
     *
     * @param visualizationOptions the visualization options
     * @param logger               the logger
     */
    public VisualizationManager(VisualizationOptions visualizationOptions, Logger logger) {
        this.visOpts = visualizationOptions;
        this.setLogger(logger);
        this.configure();
    }
    @Override
    public boolean start() {
        return true;
    }
    @Override
    public boolean stop() {
        return true;
    }
    @Override
    public boolean configure() {
        return true;
    }

    private boolean isKibanaAvailable(){
        return true;
    }
}
