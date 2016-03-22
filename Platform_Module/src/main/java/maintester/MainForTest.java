package maintester;

import platform.PlatformManager;

/**
 * Created by dude on 3/21/16.
 */
public class MainForTest {

    public static void main( String[] args )
    {
        PlatformManager pm = new PlatformManager("/home/dude/Downloads/config.json",null);
        pm.configure();
        pm.start();
//        AlgorithmsOptions dude = new AlgorithmsOptions();
//        Algorithm hi = new Algorithm(dude);
//        hi.start();
        /*Parser hi = new Parser("/home/dude/Downloads/config.json");
        hi.extractOptions();
        AlgorithmsOptions dude = hi.getAlgorithmsOptions();
        AlgorithmManager mng = new AlgorithmManager(dude)
        System.out.println(dude.getAlgorithmParameters("somealgorithm"));
        System.out.println(dude.getAlgorithmNames());*/
    }
}
