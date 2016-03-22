package parsing;

import java.util.List;

/**
 * Created by pedro on 3/18/16.
 */
public class NotificationOptions extends Options {
    public NotificationOptions(){}
    
    public List<String> getUsers(){
        return null;
    }
    
    public String getUserPhone(String user){
        return "";
    }
    
    public String getUserPhoneProvider(String user){
        return "";
    }
    
    public String getUserEmail(String user){
        return "";
    }
    
    public List<Integer> getUserAlgorithmsNotification(String user){
        return null;
    }
}
