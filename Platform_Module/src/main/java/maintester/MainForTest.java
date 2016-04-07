package maintester;

import algorithms.AlgorithmManager;
import dataretrieval.DataRetrievalManager;
import parsing.AlgorithmsOptions;
import parsing.DataRetrievalOptions;
import parsing.Parser;

import java.util.logging.Logger;

/**
 * Created by dude on 3/21/16.
 */
public class MainForTest {

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
        AlgorithmManager mng = new AlgorithmManager(dude, Logger.getLogger("NadsLogger"));
        mng.start();
    }
}
