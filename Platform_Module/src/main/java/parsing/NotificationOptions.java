package parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pedro on 3/18/16.
 */
public class NotificationOptions extends Options{
    /**
     * Instantiates a new Notification options.
     */
    public NotificationOptions(){}

    /**
     * Get email string.
     *
     * @return the string with the email
     */
    public String getEmail(){
        return this.getOpt("notificationemail").getOptionStringIndividual();
    }

    /**
     * Get email password string.
     *
     * @return the string with the email password
     */
    public String getEmailPassword(){
        return this.getOpt("notificationemailpassword").getOptionStringIndividual();
    }

    /**
     * Get path string.
     *
     * @return the string with the path to the notification module script
     */
    public String getPath(){
        return this.getOpt("notificationfilelocation").getOptionStringIndividual();
    }

    /**
     * Get notification users list.
     *
     * @return the list with the users to be notified
     */
    public List<String> getNotificationUsers(){
       List<String> out = new ArrayList<>();
       for (Option t: this.getOpt("users").getOptionList()){
           out.add(t.getOptionMap().get("name"));
       }
       return out;
    }

    /**
     * Get user information map.
     *
     * @param user string containing user whose information is going to be search
     * @return the map containing the user information
     */
    public Map<String, String> getUserInformation(String user){
        for (Option t: this.getOpt("users").getOptionList()){
            if((t.getOptionMap().get("name")).equalsIgnoreCase(user))
                return t.getOptionMap();
        }
        return null;
    }

    /**
     * Get user phone number string.
     *
     * @param user the user whose phone number is to be searched
     * @return the string with the users phone number
     */
    public String getUserPhoneNumber(String user){
        for (Option t: this.getOpt("users").getOptionList()){
            if((t.getOptionMap().get("name")).equalsIgnoreCase(user))
                return t.getOptionMap().get("phonenumber");
      }
        return null;
    }

    /**
     * Get user phone provider string.
     *
     * @param user the user whose cellphone provider is going to be searched
     * @return the string with the users cell phone provider
     */
    public String getUserPhoneProvider(String user){
        for (Option t: this.getOpt("users").getOptionList()){
            if((t.getOptionMap().get("name")).equalsIgnoreCase(user))
                return t.getOptionMap().get("phoneprovider");
        }
        return null;
    }

    /**
     * Get user email string.
     *
     * @param user the user whose email is to be searched
     * @return the string with the users email
     */
    public String getUserEmail(String user){
        for (Option t: this.getOpt("users").getOptionList()){
            if((t.getOptionMap().get("name")).equalsIgnoreCase(user))
                return t.getOptionMap().get("email");
        }
        return null;
    }

//    public List<String> getUserAlgorithms(String user){
////        return null;
//        for (Option t: this.getOpt("users").getOptionList()){
//            if((t.getOptionMap().get("name")).equalsIgnoreCase(user)){
//                return t.getOptionMap().get("notifiablealgorithms");
//            }
//        }
//        return null;
//    }

    /**
     * Get valid algorithms list.
     *
     * @return the list withthe valid algorithms
     */
    public List<String> getValidAlgorithms(){
        return this.getOpt("validalgorithmslist").getOptionStringList();
    }
}
