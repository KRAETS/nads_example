package parsing;

import com.google.gson.Gson;
/**
 * Created by dude on 3/17/16.
 */
public class Parser {
    Gson gson = new Gson();
    String configFile = "";






    public Parser(String configFile)
    {
        this.configFile = configFile;
    }
}
