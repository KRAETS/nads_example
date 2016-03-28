package maintester;

import dataretrieval.DataRetrievalManager;
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
        Parser hi = new Parser("/home/pedro/Documents/git/nads/Platform_Module/src/resources/config.json");
        hi.extractOptions();
        DataRetrievalOptions dude = hi.getDataRetrievalOptions();
        DataRetrievalManager mng = new DataRetrievalManager(dude, Logger.getLogger("NadsLogger"));
        mng.start();
    }
}
