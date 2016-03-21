package notifications;

import interfaces.Manager;
import parsing.NotificationOptions;

/**
 * Created by pedro on 3/17/16.
 */
public class NotificationManager extends Manager {
    private NotificationOptions notOpts;
    public NotificationManager(NotificationOptions notificationOptions) {
        this.notOpts = notificationOptions;
        this.configure();
    }

    public boolean start() {
        return false;
    }

    public boolean stop() {
        return false;
    }

    public boolean configure() {
        return false;
    }
}
