package startup;

import platform.PlatformManager;

/**
 * The type Launcher.
 */
public class Launcher
{
    /**
     * The entry point of application.
     * @param args input arguments
     */
    public static void main( String[] args ) {
        //TODO Use this as the new main
        System.out.println( "Initializing nads..." );
        final PlatformManager platman = new PlatformManager(args[0],args[1]);
        System.out.println("Done\n Adding shutdown hooks...");
        Runtime.getRuntime().addShutdownHook(
                new Thread() {
                    @Override
                    public void run()
                    {
                        System.out.println("Shutdown signal received... Proceeding to shutdown gracefully");
                        platman.stop();
                    }
                }
        );
        System.out.println("Done\n Starting the system...");
        platman.start();
        System.out.println("Done\n Moving to background");
    }
}
