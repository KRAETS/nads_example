package visualization;

import interfaces.Manager;
import parsing.VisualizationOptions;

/**
 * Created by pedro on 3/17/16.
 */
public class VisualizationManager extends Manager {
    private VisualizationOptions visOpts;
    public VisualizationManager(VisualizationOptions visualizationOptions) {
        this.visOpts = visualizationOptions;
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

    private boolean isKibanaAvailability(){

    }
}
