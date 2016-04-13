package notifications;

import interfaces.Manager;
import parsing.NotificationOptions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by pedro on 3/17/16.
 */
public class NotificationManager extends Manager {
    private NotificationOptions notOpts;
    private Notification notification;

    /**
     * Instantiates a new Notification manager.
     *
     * @param notificationOptions the notification options
     * @param logger              the logger
     */
    public NotificationManager(NotificationOptions notificationOptions, Logger logger) {
        this.notOpts = notificationOptions;
        this.setLogger(logger);
        this.configure();
    }

    public boolean start() {
        try{
            notification.start();
        }
        catch (Exception e){
            this.getLogger().log(Level.SEVERE,"Could not start notification: " + e.toString());
            return false;
        }
        return true;
    }

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

    public boolean configure() {
        //TODO Read options and start
        try {
            notification = new Notification(notOpts, this.getLogger());
        }
        catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Could not set up notification in manager: " + e.toString());
            return false;
        }
        return true;
    }
}


