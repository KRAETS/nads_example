package maintester;

import algorithms.Algorithm;
import parsing.AlgorithmsOptions;

/**
 * Created by dude on 3/21/16.
 */
public class MainForTest {

    public static void main( String[] args )
    {
        AlgorithmsOptions dude = new AlgorithmsOptions();
        Algorithm hi = new Algorithm(dude);
        hi.start();
    }
}
