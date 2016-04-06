package parsing;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private Gson gson = new Gson();
    private String configFile = "/home/dude/Downloads/config.json";
    private Type typeOfSOHashMap = new TypeToken<Map<String, Object>>() { }.getType();
    private Type typeOfSOSHashMap = new TypeToken<Map<String, HashMap<String,Object>>>() { }.getType();
    private Type typeOfSSHashMap = new TypeToken<Map<String, String>>() { }.getType();
    private Type typeOfHMList = new TypeToken<List<HashMap<String, HashMap<String,String>>>>() { }.getType();
    private Type typeOfHMSSList = new TypeToken<List<HashMap<String,String >>>() { }.getType();
    private PlatformOptions mainOptions = new PlatformOptions();
    private AlgorithmsOptions algorithmsOptions = new AlgorithmsOptions();
    private DataRetrievalOptions dataRetrievalOptions = new DataRetrievalOptions();
    private VisualizationOptions visualOptions = new VisualizationOptions();
    private NotificationOptions notificationsOptions = new NotificationOptions();
    private UtilitiesOptions utilitiesOptions = new UtilitiesOptions();

    public boolean extractOptions()
    {
        //Set character encoding for when the config file is read
        Charset cs = Charset.forName("UTF-8");
        //try catch for IO errors resulting from reading the file
        try
        {
            //Read config file as a single long string
            String dude = this.readFile(configFile,cs);
            //Try catch for parsing errors in the json format
            try
            {
                //Creates Map of whole config file
                Map<String, Object> newMap = gson.fromJson(dude,typeOfSOHashMap);
                //Calls function to parse te options for each module
                this.utilitiesOptionsParsers(newMap);
                this.mainOptionsParsers(newMap);
                this.visualOptionsParsers(newMap);
                this.dataRetrievalOptionsParsers(newMap);
                this.algorithmsOptionsParsers(newMap);
                this.notificationOptionsParsers(newMap);
            }catch (JsonParseException a)
            {
                //Send notification to user of bad config file format
                return false;
            }
        }catch(IOException e)
        {
            //Send notification to user that config file could not be found
            return false;
        }

        return true;

    }

    private void mainOptionsParsers(Map<String, Object> options)
    {
        try
        {
            //Extracts platform options from main map
            Map<String, String> mainMap = gson.fromJson(gson.toJson(options.get("main")).toString(), typeOfSSHashMap);

            for (Map.Entry<String, String> entry : mainMap.entrySet()) {
                mainOptions.addOption(entry.getKey(), new Option(entry.getKey(), entry.getValue(), Option.OptionType.INDIVIDUAL));
                //System.out.println(entry.getKey() + "/" + entry.getValue());
            }

        }catch (JsonParseException a)
        {
            //Send notification to user of bad config file format in the platform options
            throw new JsonParseException("");
        }
    }

    private void algorithmsOptionsParsers(Map<String, Object> options)
    {
        try
        {
            //Extracts algorithms options from main map
            List<HashMap<String, HashMap<String, String>>> algorithmsMap = gson.fromJson(gson.toJson(options.get("algorithms")).toString(), typeOfHMList);
            //List<HashMap<String,String>> algorithmsMap = gson.fromJson(options.get("algorithms").toString(),typeOfHMList);
            for (HashMap<String, HashMap<String, String>> hmap : algorithmsMap) {
                for (Map.Entry<String, HashMap<String, String>> entry : hmap.entrySet()) {
                    Option tempOpt = new Option(entry.getKey(), Option.OptionType.OPTIONMAP);
                    tempOpt.setOptionMap(entry.getValue());
                    algorithmsOptions.addOption(entry.getKey(), tempOpt);
                    //System.out.println(entry.getKey() + "/" + entry.getValue());
                }

            }
        }catch(JsonParseException a)
        {
            //Send notification to user of bad config file format in the algorithms options
            throw new JsonParseException("");
        }
    }

    private void utilitiesOptionsParsers(Map<String, Object> options)
    {
        try {
            //Extracts utitlies options from main map
            Map<String, String> utilitiesMap = gson.fromJson(gson.toJson(options.get("utilities")).toString(), typeOfSSHashMap);
            for (Map.Entry<String, String> entry : utilitiesMap.entrySet()) {
                utilitiesOptions.addOption(entry.getKey(), new Option(entry.getKey(), entry.getValue(), Option.OptionType.INDIVIDUAL));
                //System.out.println(entry.getKey() + "/" + entry.getValue());
            }
        }catch (JsonParseException a)
        {
            //Send notification to user of bad config file format in the utilities options
            throw new JsonParseException("");
        }
    }

    private void dataRetrievalOptionsParsers(Map<String, Object> options)
    {
        try {
            //Extracts data retrieval options from main map
            Map<String, String> dataRetrievalMap = gson.fromJson(gson.toJson(options.get("dataretrieval")).toString(), typeOfSSHashMap);
            for (Map.Entry<String, String> entry : dataRetrievalMap.entrySet()) {
                dataRetrievalOptions.addOption(entry.getKey(), new Option(entry.getKey(), entry.getValue(), Option.OptionType.INDIVIDUAL));
                //System.out.println(entry.getKey() + "/" + entry.getValue());
            }
        }catch (JsonParseException a)
        {
            //Send notification to user of bad config file format in the data retrieval options
            throw new JsonParseException("");
        }
    }

    private void notificationOptionsParsers(Map<String, Object> options)
    {
        try {
            //Extracts notifications options from main map
            Map<String, Object> notificationMap = gson.fromJson(gson.toJson(options.get("notification")).toString(), typeOfSOHashMap);
            for (Map.Entry<String, Object> entry : notificationMap.entrySet()) {
                if (entry.getKey().equals("users")) {
                    List<HashMap<String, String>> usersMap = gson.fromJson(gson.toJson(entry.getValue()).toString(), typeOfHMSSList);
                    Option tempOpt1 = new Option(entry.getKey(), new ArrayList<Option>(), Option.OptionType.OPTIONLIST);
                    for (HashMap<String, String> hmap : usersMap) {
                        Option tempOpt = new Option(entry.getKey(), Option.OptionType.OPTIONMAP);
                        tempOpt.setOptionMap(hmap);
                        tempOpt1.addToOptionList(tempOpt);
                        //System.out.println(entry.getKey() + "/" + entry.getValue());
                    }
                    notificationsOptions.addOption(entry.getKey(), tempOpt1);
                } else {
                    notificationsOptions.addOption(entry.getKey(), new Option(entry.getKey(), entry.getValue().toString(), Option.OptionType.INDIVIDUAL));
                    //System.out.println(entry.getKey() + "/" + entry.getValue());
                }

            }
        }catch (JsonParseException a)
        {
            //Send notification to user of bad config file format in the notification options
            throw new JsonParseException("");
        }
    }

    private void visualOptionsParsers(Map<String, Object> options)
    {
        try {
            //Extracts visual options from main map
            Map<String, String> visualMap = gson.fromJson(gson.toJson(options.get("visual")).toString(), typeOfSSHashMap);
            for (Map.Entry<String, String> entry : visualMap.entrySet()) {
                visualOptions.addOption(entry.getKey(), new Option(entry.getKey(), entry.getValue(), Option.OptionType.INDIVIDUAL));
                //System.out.println(entry.getKey() + "/" + entry.getValue());
            }
        }catch (JsonParseException a)
        {
            //Send notification to user of bad config file format in the platform options
            throw new JsonParseException("");
        }
    }


    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public PlatformOptions getPlatformOptions() {
        return mainOptions;
    }

    public NotificationOptions getNotificationOptions() {
        return notificationsOptions;
    }

    public UtilitiesOptions getUtilitiesOptions() {
        return utilitiesOptions;
    }

    public DataRetrievalOptions getDataRetrievalOptions() {
        return dataRetrievalOptions;
    }

    public VisualizationOptions getVisualizationOptions() {
        return visualOptions;
    }

    public AlgorithmsOptions getAlgorithmsOptions() {
        return algorithmsOptions;
    }


    public Parser(String configFile)
    {
        this.configFile = configFile;
    }
}

