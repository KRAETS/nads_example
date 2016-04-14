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
     * @param visualizationOptions visualization options
     * @param logger               class logger
     */
    public VisualizationManager(VisualizationOptions visualizationOptions, Logger logger) {
        this.visOpts = visualizationOptions;
        this.setLogger(logger);
        this.configure();
    }

    /**
     * Starts visualization tool.
     * @return boolean, visualization tool startup status
     */
    @Override
    public boolean start() {
        return true;
    }

    /**
     * Stops the visualization tool.
     * @return boolean, visualization tool stopped status
     */
    @Override
    public boolean stop() {
        return true;
    }

    /**
     * Configures the visualization tool.
     * @return boolean, visualization tool configuration status
     */
    @Override
    public boolean configure() {
        return true;
    }

    /**
     * Indicates if visualization tool is active.
     * @return boolean, status of the visualization tool
     */
    private boolean isKibanaAvailable(){
        return true;
    }
}
