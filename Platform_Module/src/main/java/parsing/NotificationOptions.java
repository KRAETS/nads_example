package parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pedro on 3/18/16.
 */
public class NotificationOptions extends Options {
    public NotificationOptions(){}
    
    public List<String> getUsers(){
        return new ArrayList<String>(this.optionMap.keySet());
    }
    
    public Map<String, String> getUserInformation(String user){
        return this.getOpt(user).getOptionMap();
    }
    
    public String getUserName(String user){
        return this.getOpt(user).getOptionMap()("name");
    }
    
    public String getUserPhoneNumber(String user){
        return this.getOpt(user).getOptionMap()("phonenumber");
    }
    
    public String getUserPhoneProvider(String user){
        return this.getOpt(user).getOptionMap()("phoneprovider");
    }
    
    public String getUserEmail(String user){
        return this.getOpt(user).getOptionMap()("email");
    }
    
    public List<String> getUserAlgorithm(String user){
        return this.getOpt(user).getOptionList()("notifiablealgorithms");
    }
}
