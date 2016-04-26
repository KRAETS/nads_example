package maintester;

import algorithms.AlgorithmManager;
import dataretrieval.DataRetrievalManager;
import notifications.NotificationManager;
import parsing.AlgorithmsOptions;
import parsing.NotificationOptions;
import parsing.Option;
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
        NotificationOptions notificationOptions = hi.getNotificationOptions();
        Option algorithmNames = new Option("validalgorithmslist", Option.OptionType.STRINGLIST);
        algorithmNames.setOptionStringList(hi.getAlgorithmsOptions().getAlgorithmNames());
        notificationOptions.addOption("validalgorithmslist",algorithmNames);
        NotificationManager nm = new NotificationManager(notificationOptions,Logger.getLogger("NadsLogger"));
        nm.configure();
        nm.start();
        AlgorithmManager mng = new AlgorithmManager(dude, Logger.getLogger("NadsLogger"));
        mng.start();
    }
}
