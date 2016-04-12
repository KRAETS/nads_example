package startup;

import platform.PlatformManager;

public class Launcher
{
    public static void main( String[] args )
    {
        //TODO Use this as the new main
        System.out.println( "Initializing nads..." );
        final PlatformManager platman = new PlatformManager(args[0],args[1]);
        System.out.println("Adding shutdown hooks...");
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                System.out.println("Shutdown signal received... Proceeding to shutdown gracefully");
                platman.stop();
            }
        });
        System.out.println("Starting the system...");
        platman.start();
    }
}
