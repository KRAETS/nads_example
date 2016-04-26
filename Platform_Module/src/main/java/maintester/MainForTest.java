package maintester;

import algorithms.AlgorithmManager;
import dataretrieval.DataRetrievalManager;
import parsing.AlgorithmsOptions;
import parsing.Parser;

import java.util.logging.Logger;

/**
 * Created by dude on 3/21/16.
 */
public class MainForTest {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main( String[] args )
    {
//        PlatformManager pm = new PlatformManager("/home/dude/Downloads/config.json",null);
//        pm.configure();
//        pm.start();
//        AlgorithmsOptions dude = new AlgorithmsOptions();
//        Algorithm hi = new Algorithm(dude);
//        hi.start();
        Parser hi = new Parser("../Platform_Module/src/resources/config.json");
        hi.extractOptions();
        AlgorithmsOptions dude = hi.getAlgorithmsOptions();
        DataRetrievalManager dm = new DataRetrievalManager(hi.getDataRetrievalOptions(),Logger.getLogger("NadsLogger") );
        dm.configure();
        dm.start();
        AlgorithmManager mng = new AlgorithmManager(dude, Logger.getLogger("NadsLogger"));
        //mng.start();
    }
}
