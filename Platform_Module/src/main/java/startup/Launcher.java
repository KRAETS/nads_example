package startup;

import platform.PlatformManager;

public class Launcher
{
    public static void main( String[] args )
    {
        //TODO Use this as the new main
        System.out.println( "Initializing nads..." );
        PlatformManager platman = new PlatformManager(args[0],args[1]);
        System.out.println("Adding shutdown hooks...");
        /*Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                platman.stop();
            }
        });*/
        System.out.println("Starting the system...");
        platman.start();
    }

    /**
     * Method that starts the platform as a service
     */
    public static void start(){
    }

    /**
     * Method that stops the platform service
     */
    public static void stop(){

    }

    /**
     * Method that installs the service into the system for later starting/stopping
     */
    public static void install(){

    }
}
