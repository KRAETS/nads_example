package parsing;

import java.util.List;
import java.util.Map;

/**
 * Created by pedro on 3/18/16.
 */
public class NotificationOptions extends Options{
    public NotificationOptions(){}
    
    public String getNotificationPath(){
        return this.getOpt("notificationfolder").getOptionStringIndividual();
    }

    public List<String> getNotificationUsers(){
       List<String> out = null;
       for (Option t: this.getOpt("users").getOptionList()){
           out.add(t.getOptionMap().get("name"));
       }
       return out;
    }

    public Map<String, String> getUserInformation(String user){
        for (Option t: this.getOpt("users").getOptionList()){
            if((t.getOptionMap().get("name")).equalsIgnoreCase(user))
                return t.getOptionMap();
        }
        return null;
    }
    
    public String getUserPhoneNumber(String user){
        for (Option t: this.getOpt("users").getOptionList()){
            if((t.getOptionMap().get("name")).equalsIgnoreCase(user))
                return t.getOptionMap().get("phonenumber");
      }
        return null;
    }
    
    public String getUserPhoneProvider(String user){
        for (Option t: this.getOpt("users").getOptionList()){
            if((t.getOptionMap().get("name")).equalsIgnoreCase(user))
                return t.getOptionMap().get("phoneprovider");
        }
        return null;
    }
    
    public String getUserEmail(String user){
        for (Option t: this.getOpt("users").getOptionList()){
            if((t.getOptionMap().get("name")).equalsIgnoreCase(user))
                return t.getOptionMap().get("email");
        }
        return null;
    }

    public String getUserAlgorithm(String user){
        for (Option t: this.getOpt("users").getOptionList()){
            if((t.getOptionMap().get("name")).equalsIgnoreCase(user))
                return t.getOptionMap().get("notifiablealgorithms");
        }
        return null;
    }
}
