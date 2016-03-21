package parsing;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.xml.internal.ws.api.ha.StickyFeature;


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

    public void extractOptions()
    {
        List<String> lines;
        Charset cs = Charset.forName("UTF-8");
        try
        {

            lines = Files.readAllLines(Paths.get(configFile), cs);
            String dude = this.readFile(configFile,cs);
            //dude = gson.toJson(dude).toString();
            Map<String, Object> newMap = gson.fromJson(dude,typeOfSOHashMap);
            Map<String, Object> newMap2 = gson.fromJson(newMap.get("main").toString(),typeOfSOHashMap);
            String hello = gson.toJson(newMap.get("visual")).toString();
            //String hello = newMap.get("visual").toString();
            Map<String, Object> algorithmsMap = gson.fromJson(hello,typeOfSOHashMap);
            //System.out.println(algorithmsMap.get("users").toString());
            this.utilitiesOptionsParsers(newMap);
            this.mainOptionsParsers(newMap);
            this.notificationOptionsParsers(newMap);
            this.visualOptionsParsers(newMap);
            this.dataRetrievalOptionsParsers(newMap);
            this.algorithmsOptionsParsers(newMap);
        }catch(IOException e){}


    }

    private void mainOptionsParsers(Map<String, Object> options)
    {
        Map<String, String> mainMap = gson.fromJson(gson.toJson(options.get("main")).toString(),typeOfSSHashMap);
        for (Map.Entry<String, String> entry : mainMap.entrySet())
        {
            mainOptions.addOption(entry.getKey(),new Option(entry.getKey(),entry.getValue(), Option.OptionType.INDIVIDUAL));
            //System.out.println(entry.getKey() + "/" + entry.getValue());
        }
    }

    private void algorithmsOptionsParsers(Map<String, Object> options)
    {
        List<HashMap<String, HashMap<String,String>>> algorithmsMap = gson.fromJson(gson.toJson(options.get("algorithms")).toString(),typeOfHMList);
        //List<HashMap<String,String>> algorithmsMap = gson.fromJson(options.get("algorithms").toString(),typeOfHMList);
        for (HashMap<String, HashMap<String,String>> hmap: algorithmsMap)
        {
            for (Map.Entry<String, HashMap<String,String>> entry : hmap.entrySet())
            {
                Option tempOpt = new Option(entry.getKey(), Option.OptionType.OPTIONMAP);
                tempOpt.setOptionMap(entry.getValue());
                algorithmsOptions.addOption(entry.getKey(),tempOpt);
                //System.out.println(entry.getKey() + "/" + entry.getValue());
            }

        }
    }

    private void utilitiesOptionsParsers(Map<String, Object> options)
    {
        Map<String, String> utilitiesMap = gson.fromJson(gson.toJson(options.get("utilities")).toString(),typeOfSSHashMap);
        for (Map.Entry<String, String> entry : utilitiesMap.entrySet())
        {
            utilitiesOptions.addOption(entry.getKey(),new Option(entry.getKey(),entry.getValue(), Option.OptionType.INDIVIDUAL));
            //System.out.println(entry.getKey() + "/" + entry.getValue());
        }
    }

    private void dataRetrievalOptionsParsers(Map<String, Object> options)
    {
        Map<String, String> dataRetrievalMap = gson.fromJson(gson.toJson(options.get("dataretrieval")).toString(),typeOfSSHashMap);
        for (Map.Entry<String, String> entry : dataRetrievalMap.entrySet())
        {
            dataRetrievalOptions.addOption(entry.getKey(),new Option(entry.getKey(),entry.getValue(), Option.OptionType.INDIVIDUAL));
            //System.out.println(entry.getKey() + "/" + entry.getValue());
        }
    }

    private void notificationOptionsParsers(Map<String, Object> options)
    {
        Map<String, Object> notificationMap = gson.fromJson(gson.toJson(options.get("notification")).toString(),typeOfSOHashMap);
        for (Map.Entry<String, Object> entry : notificationMap.entrySet())
        {
            if (entry.getKey().equals("users"))
            {
                List<HashMap<String,String>> usersMap = gson.fromJson(gson.toJson(entry.getValue()).toString(),typeOfHMSSList);
                Option tempOpt1 = new Option(entry.getKey(),new ArrayList<Option>(), Option.OptionType.OPTIONLIST);
                for (HashMap<String, String > hmap: usersMap)
                {
                    Option tempOpt = new Option(entry.getKey(), Option.OptionType.OPTIONMAP);
                    tempOpt.setOptionMap(hmap);
                    tempOpt1.addToOptionList(tempOpt);
                    //System.out.println(entry.getKey() + "/" + entry.getValue());
                }
                notificationsOptions.addOption(entry.getKey(),tempOpt1);
            }
            else
            {
                notificationsOptions.addOption(entry.getKey(),new Option(entry.getKey(),entry.getValue().toString(), Option.OptionType.INDIVIDUAL));
                //System.out.println(entry.getKey() + "/" + entry.getValue());
            }

        }
    }

    private void visualOptionsParsers(Map<String, Object> options)
    {
        Map<String, String> visualMap = gson.fromJson(gson.toJson(options.get("visual")).toString(),typeOfSSHashMap);
        for (Map.Entry<String, String> entry : visualMap.entrySet())
        {
            visualOptions.addOption(entry.getKey(),new Option(entry.getKey(),entry.getValue(), Option.OptionType.INDIVIDUAL));
            //System.out.println(entry.getKey() + "/" + entry.getValue());
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
        //this.configFile = configFile;
    }
}

