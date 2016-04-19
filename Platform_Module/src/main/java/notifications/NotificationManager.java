package notifications;

import interfaces.Manager;
import parsing.NotificationOptions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by marie on 3/17/16.
 */
public class NotificationManager extends Manager {
    private NotificationOptions notOpts;
    private Notification notification;

    /**
     * Instantiates a new Notification manager.
     * @param notificationOptions the notification options
     * @param logger              the logger of the class
     */
    public NotificationManager(NotificationOptions notificationOptions, Logger logger) {
        this.notOpts = notificationOptions;
        this.setLogger(logger);
        this.configure();
    }

    /**
     * Starts the notification script.
     * @return boolean, indicates if notification was successfully started.
     */
    public boolean start() {
        try{
            this.getLogger().log(Level.INFO,"Starting notification system");
            notification.start();
        }
        catch (Exception e){
            this.getLogger().log(Level.SEVERE,"Could not start notification: " + e.toString());
            return false;
        }
        return true;
    }

    /**
     * Stops the notification script.
     * @return boolean, indicates if notification was successfully stopped.
     */
    public boolean stop() {
        try{
            notification.stop();
        }
        catch (Exception e){
            this.getLogger().log(Level.SEVERE,"Could not stop notification: " + e.toString());
            return false;
        }
        return true;
    }

    /**
     * Configures the notification script.
     * @return boolean, indicates if notification was successfully configured.
     */
    public boolean configure() {
        try {
            this.getLogger().log(Level.INFO,"Initializing notification system");
            notification = new Notification(notOpts, this.getLogger());
            this.getLogger().log(Level.INFO,"Done");
        }
        catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Could not set up notification in manager: " + e.toString());
            return false;
        }
        return true;
    }
}


