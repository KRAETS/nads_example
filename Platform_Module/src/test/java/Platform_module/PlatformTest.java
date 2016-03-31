package Platform_module;

import algorithms.AlgorithmManager;
import dataretrieval.DataRetrievalManager;
import notifications.NotificationManager;
import org.junit.Test;
import parsing.Parser;
import platform.PlatformManager;
import utilities.UtilitiesManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.junit.Assert.*;

/**
 * Created by dude on 3/22/16.
 */
public class PlatformTest {

    //Method tests that the platform options were correctly parsed
    @Test
    public void platformOptionsParsingTest()
    {
        String configFile = "src/resources/config.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        assertEquals("something", p.getPlatformOptions().getWorkingDirectory());
        assertEquals("nads.log", p.getPlatformOptions().getLogLocation());
    }

    //Method tests that the data retrieval options were correctly parsed
    @Test
    public void dataRetrievalOptionsParsingTest()
    {
        String configFile = "src/resources/config.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        Map<String,String> m = new HashMap<String, String>();
        m.put("elasticsearchaddress","127.0.0.2");
        m.put("logstashaddress","127.0.0.1");
        m.put("elasticsearchport","70002");
        m.put("logstashport","70001");
        assertEquals("4","127.0.0.2", p.getDataRetrievalOptions().getElasticSearchAddress());
        assertEquals("3","70002", p.getDataRetrievalOptions().getElasticSearchPort());
        assertEquals("2","127.0.0.1", p.getDataRetrievalOptions().getLogstashAddress());
        assertEquals("1","70001", p.getDataRetrievalOptions().getLogstashPort());
        assertEquals("5",m, p.getDataRetrievalOptions().getDataRetrievalParameters());

    }

    //Method tests that the visualization options were correctly parsed
    @Test
    public void visualizationOptionsParsingTest()
    {
        String configFile = "src/resources/config.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        assertEquals("127.0.0.0", p.getVisualizationOptions().getKibanaAddress());
        assertEquals("70000", p.getVisualizationOptions().getKibanaPort());
        assertEquals("pathtotemplatedumpingfolder", p.getVisualizationOptions().getKibanaTemplateFolder());
    }

    //Method tests that the notification options were correctly parsed
    @Test
    public void notificationOptionsParsingTest()
    {
        String configFile = "src/resources/config.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        List<String> l = new ArrayList<String>();
        l.add("someone");
        Map<String,String> m = new HashMap<String, String>();
        m.put("name","someone");
        m.put("notifiablealgorithms","[\"testalgorithm\"]");
        m.put("phonenumber","7871234567");
        m.put("phoneprovider","claro");
        m.put("email","a@b.com");
//        assertEquals(l, p.getNotificationOptions().getUsers());
        assertEquals("[\"testalgorithm\"]", p.getNotificationOptions().getUserAlgorithm("someone"));
        assertEquals("a@b.com", p.getNotificationOptions().getUserEmail("someone"));
        assertEquals("7871234567", p.getNotificationOptions().getUserPhoneNumber("someone"));
        assertEquals("claro", p.getNotificationOptions().getUserPhoneProvider("someone"));
        assertEquals(m, p.getNotificationOptions().getUserInformation("someone"));
    }

    //Method tests that the algorithm options were correctly parsed
    @Test
    public void algorithmsOptionsParsingTest()
    {
        String configFile = "src/resources/config.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        Map<String,String> m = new HashMap<String, String>();
        m.put("folder","../algorithms/loop_detec/test.py");
        m.put("model","values");
        m.put("ips","[\"136.145.59.152\"]");
        m.put("exampleip","value");
        m.put("trap_oid","value");
        assertEquals("[testalgorithm]", p.getAlgorithmsOptions().getAlgorithmNames().toString());
        assertEquals(m, p.getAlgorithmsOptions().getAlgorithmParameters("testalgorithm"));
    }

    @Test
    public void utilitiesOptionsParsingTest()
    {
        String configFile = "src/resources/config.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        Map<String,String> m = new HashMap<String, String>();
        m.put("newpatternfilelocation","/home/pedro/Desktop/10-syslog-filter.conf");
        m.put("originalpatternfilelocation","/etc/logstash/conf.d/10-syslog-filter.conf");
//        assertEquals(l, p.getNotificationOptions().getUsers());
        assertEquals(m.get("newpatternfilelocation"), p.getUtilitiesOptions().getNewPatternFileLocation());
        assertEquals(m.get("originalpatternfilelocation"), p.getUtilitiesOptions().getOriginalPatternFileLocation());
    }
    //Method that tests whether an essential field was left out or not in configurations
    @Test
    public void verifyIncompleteConfiguration()
    {
        String configFile = "src/resources/missingessentialfieldconfig.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        //missing folder
        String name = p.getAlgorithmsOptions().getAlgorithmNames().get(0);
        assertNull(p.getAlgorithmsOptions().getAlgorithmFolder(name));

    }
    //Method tests that the parser detects a nonexistent json file
    @Test
    public void nonexistantJsonParsingTest()
    {
        String configFile = "src/resources/conerrfig.json";
        Parser p = new Parser(configFile);
        assertFalse(p.extractOptions());

    }

    //Method tests that the parser detects a incorrect json file
    @Test
    public void incorrectSyntaxJsonParsingTest()
    {
        String configFile = "src/resources/wrongformat.json";
        Parser p = new Parser(configFile);
        assertFalse(p.extractOptions());
    }
    //Method tests that the parser detects a syntactically correct json file but with the wrong fields
    @Test
    public void incorrectFieldsJsonParsingTest()
    {
        String configFile = "src/resources/correctsyntaxwrongfields.json";
        Parser p = new Parser(configFile);
        assertFalse(p.extractOptions());
    }

    //Method tests that the platform manager was correctly initialized
    @Test
    public void platformInitializingTest()
    {
        String configFile = "src/resources/config.json";
        PlatformManager pm = new PlatformManager(configFile, null);
        assertTrue(pm.configure());
        assertTrue(pm.start());
        pm.stop();
    }

    //Method tests that the algorithm manager was correctly initialized
    @Test
    public void algorithmInitializingTest()
    {
        Logger logMan = Logger.getLogger("NadsLogger");
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler("nadsplatformtest.log");
            logMan.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            // the following statement is used to log any messages
            logMan.info("Logger started");

        } catch (SecurityException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
        String configFile = "src/resources/config.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        AlgorithmManager am  = new AlgorithmManager(p.getAlgorithmsOptions(), logMan);
        assertTrue(am.configure());
        assertTrue(am.start());
    }

    //Method tests that the data retrieval manager was correctly initialized
    @Test
    public void dataRetrievalInitializingTest()
    {
        Logger logMan = Logger.getLogger("NadsLogger");
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler("nadsplatformtest.log");
            logMan.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            // the following statement is used to log any messages
            logMan.info("Logger started");

        } catch (SecurityException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
        String configFile = "src/resources/config.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        DataRetrievalManager drm = new DataRetrievalManager(p.getDataRetrievalOptions(),logMan);
        assertTrue(drm.configure());
        assertTrue(drm.start());
        drm.stop();
    }

    //Method tests that the utilities manager was correctly initialized
    @Test
    public void utilitiesInitializingTest()
    {
        Logger logMan = Logger.getLogger("NadsLogger");
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler("nadsplatformtest.log");
            logMan.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            // the following statement is used to log any messages
            logMan.info("Logger started");

        } catch (SecurityException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
        String configFile = "src/resources/config.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        UtilitiesManager um = new UtilitiesManager(p.getUtilitiesOptions(),logMan);
        assertTrue(um.configure());
        assertTrue(um.start());
    }

    //Method tests that the notification manager was correctly initialized
    @Test
    public void notificationInitializingTest()
    {
        Logger logMan = Logger.getLogger("NadsLogger");
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler("nadsplatformtest.log");
            logMan.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            // the following statement is used to log any messages
            logMan.info("Logger started");

        } catch (SecurityException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
        String configFile = "src/resources/config.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        NotificationManager um = new NotificationManager(p.getNotificationOptions(),logMan);
        assertTrue(um.configure());
        assertTrue(um.start());
    }

    @Test
    public void serviceStartTest() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("/Users/pedro/Documents/git/nads/Platform_Module/src/resources/yajsw-beta-12.05/bin/startDaemon.sh");
        p.waitFor();
        int ret = p.exitValue();
        assertEquals(0,ret);
    }

    @Test
    public void serviceStopTest() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("/Users/pedro/Documents/git/nads/Platform_Module/src/resources/yajsw-beta-12.05/bin/stopDaemon.sh");
        p.waitFor();
        int ret = p.exitValue();
        assertEquals(0,ret);
    }

    @Test
    public void serviceCrashTest() throws IOException, InterruptedException {
        //TODO
        Process p = Runtime.getRuntime().exec("/Users/pedro/Documents/git/nads/Platform_Module/src/resources/yajsw-beta-12.05/bin/stopDaemon.sh");
        p.waitFor();
        int ret = p.exitValue();
        assertEquals(0,ret);
    }

    @Test
    public void selectFromWhereKQLTest() throws IOException, InterruptedException {
        StringBuilder output2 = new StringBuilder();
//								Process p = Runtime.getRuntime().exec(new String[]{"php5", finalScriptName, param});
//
//								StringBuilder result = new StringBuilde
        String bodyargument = "SELECT \\ ALL*{protocol,portnumber,status,id,ip_address} \\ from \\ ALL/{protocol,portnumber,status,id,ip_address} \\ where \\ ALL*status \\=\"Failed\"";
        URL url = new URL("http://localhost:9200/_kql?kql="+ URLEncoder.encode( bodyargument, "UTF-8"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            output2.append(line);
        }
        rd.close();



        assertEquals(200,conn.getResponseCode());

    }


}
