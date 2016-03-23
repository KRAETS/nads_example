package parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pedro on 3/18/16.
 */
public class NotificationOptions extends Options{
    public NotificationOptions(){}
    
   public List<String> getUsers(){
        return new ArrayList<String>(this.optionMap.keySet());
    }
    
    public Map<String, String> getUserInformation(String user){
        Map<String, String> u = null;
        for (Option t: this.getOpt("users").getOptionList()){
            u.put(t.getOptionName(),t.getOptionStringIndividual());
        }
        return u;
    }
    
    public String getUserName(String user){
        for (Option t: this.getOpt("users").getOptionList()){
            if(t.getOptionName().equals(user))
                return t.getOptionMap().get("name");
        }
        return null;
    }
    
    public String getUserPhoneNumber(String user){
        for (Option t: this.getOpt("users").getOptionList()){
            if(t.getOptionName().equals(user))
                return t.getOptionMap().get("phonenumber");
        }
        return null;
    }
    
    public String getUserPhoneProvider(String user){
        for (Option t: this.getOpt("users").getOptionList()){
            if(t.getOptionName().equals(user))
                return t.getOptionMap().get("phoneprovider");
        }
        return null;
    }
    
    public String getUserEmail(String user){
        for (Option t: this.getOpt("users").getOptionList()){
            if(t.getOptionName().equals(user))
                return t.getOptionMap().get("email");
        }
        return null;
    }

    public String getUserAlgorithm(String user){
        for (Option t: this.getOpt("users").getOptionList()){
            if(t.getOptionName().equals(user))
                return t.getOptionMap().get("notifiablealgorithms");
        }
        return null;
    }
}
