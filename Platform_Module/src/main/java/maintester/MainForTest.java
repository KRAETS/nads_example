package maintester;

import platform.PlatformManager;

/**
 * Created by dude on 3/21/16.
 */
public class MainForTest {

    public static void main( String[] args )
    {
        PlatformManager pm = new PlatformManager("/Users/pedro/Documents/git/nads/Platform_Module/src/resources/config.json",null);
        pm.configure();
        pm.start();
//        AlgorithmsOptions dude = new AlgorithmsOptions();
//        Algorithm hi = new Algorithm(dude);
//        hi.start();
    }
}
