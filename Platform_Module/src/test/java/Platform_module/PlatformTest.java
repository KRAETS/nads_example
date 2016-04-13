package Platform_module;

import algorithms.AlgorithmManager;
import dataretrieval.DataRetrievalManager;
import notifications.NotificationManager;
import org.junit.Test;
import parsing.DataRetrievalOptions;
import parsing.NotificationOptions;
import parsing.Option;
import parsing.Parser;
import platform.PlatformManager;
import utilities.UtilitiesManager;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.junit.Assert.*;

/**
 * Created by antoine on 3/22/16.
 */
public class PlatformTest {

    /**
     * Tests that the platform options were correctly parsed
     */
    @Test
    public void platformOptionsParsingTest()
    {
        String configFile = "src/resources/testconfig.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        assertEquals("something", p.getPlatformOptions().getWorkingDirectory());
        assertEquals("nads.log", p.getPlatformOptions().getLogLocation());
    }

    /**
     * Tests that the data retrieval options were correctly parsed
     */
    @Test
    public void dataRetrievalOptionsParsingTest()
    {
        String configFile = "src/resources/testconfig.json";
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

    /**
     * Tests that the visualization options were correctly parsed
     */
    @Test
    public void visualizationOptionsParsingTest()
    {
        String configFile = "src/resources/testconfig.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        assertEquals("127.0.0.0", p.getVisualizationOptions().getKibanaAddress());
        assertEquals("70000", p.getVisualizationOptions().getKibanaPort());
        assertEquals("pathtotemplatedumpingfolder", p.getVisualizationOptions().getKibanaTemplateFolder());
    }

    /**
     * Tests that the notification options were correctly parsed
     */
    @Test
    public void notificationOptionsParsingTest()
    {
        String configFile = "src/resources/testconfig.json";
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
//        assertEquals("[\"testalgorithm\"]", p.getNotificationOptions().getUserAlgorithm("someone"));
        assertEquals("a@b.com", p.getNotificationOptions().getUserEmail("someone"));
        assertEquals("7871234567", p.getNotificationOptions().getUserPhoneNumber("someone"));
        assertEquals("claro", p.getNotificationOptions().getUserPhoneProvider("someone"));
        assertEquals(m, p.getNotificationOptions().getUserInformation("someone"));
    }

    /**
     * Tests that the algorithm options were correctly parsed
     */
    @Test
    public void algorithmsOptionsParsingTest()
    {
        String configFile = "src/resources/testconfig.json";
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

    /**
     * Tests that the utility options were correctly parsed.
     */
    @Test
    public void utilitiesOptionsParsingTest()
    {
        String configFile = "src/resources/testconfig.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        Map<String,String> m = new HashMap<String, String>();
        m.put("newpatternfilelocation","/home/pedro/Desktop/10-syslog-filter.conf");
        m.put("originalpatternfilelocation","/etc/logstash/conf.d/10-syslog-filter.conf");
//        assertEquals(l, p.getNotificationOptions().getUsers());
        assertEquals(m.get("newpatternfilelocation"), p.getUtilitiesOptions().getNewPatternFileLocation());
        assertEquals(m.get("originalpatternfilelocation"), p.getUtilitiesOptions().getOriginalPatternFileLocation());
    }

    /**
     * Tests whether an essential field was left out or not in configurations
     */
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

    /**
     * Tests that the parser detects a nonexistent json file
     */
    @Test
    public void nonexistantJsonParsingTest()
    {
        String configFile = "src/resources/conerrfig.json";
        Parser p = new Parser(configFile);
        assertFalse(p.extractOptions());

    }

    /**
     * Tests that the parser detects a incorrect json file
     */
    @Test
    public void incorrectSyntaxJsonParsingTest()
    {
        String configFile = "src/resources/wrongformat.json";
        Parser p = new Parser(configFile);
        assertFalse(p.extractOptions());
    }

    /**
     * Tests that the parser detects a syntactically correct json file but with the wrong fields
     */
    @Test
    public void incorrectFieldsJsonParsingTest()
    {
        String configFile = "src/resources/correctsyntaxwrongfields.json";
        Parser p = new Parser(configFile);
        assertFalse(p.extractOptions());
    }

    /**
     * Tests that the platform manager was correctly initialized
     */
    @Test
    public void platformInitializingTest()
    {
        String configFile = "src/resources/config.json";
        PlatformManager pm = new PlatformManager(configFile, null);
        assertTrue(pm.configure());
        assertTrue(pm.start());
        pm.stop();
    }

    /**
     * Tests that the algorithm manager was correctly initialized
     */
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
        String configFile = "src/resources/testconfig.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        AlgorithmManager am  = new AlgorithmManager(p.getAlgorithmsOptions(), logMan);
        assertTrue(am.configure());
        assertTrue(am.start());
    }

    /**
     * Tests that the data retrieval manager was correctly initialized
     */
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
        String configFile = "src/resources/testconfig.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        DataRetrievalManager drm = new DataRetrievalManager(p.getDataRetrievalOptions(),logMan);
        assertTrue(drm.configure());
        assertTrue(drm.start());
        drm.stop();
    }

    /**
     * Tests that the utilities manager was correctly initialized
     */
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
        String configFile = "src/resources/testconfig.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        UtilitiesManager um = new UtilitiesManager(p.getUtilitiesOptions(),logMan);
        assertTrue(um.configure());
        assertTrue(um.start());
    }

    /**
     * Tests that the notification manager was correctly initialized
     */
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
        String configFile = "src/resources/testconfig.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        NotificationOptions notificationOptions = p.getNotificationOptions();
        Option algorithmNames = new Option("validalgorithmslist", Option.OptionType.STRINGLIST);
        algorithmNames.setOptionStringList(p.getAlgorithmsOptions().getAlgorithmNames());
        notificationOptions.addOption("validalgorithmslist",algorithmNames);
        NotificationManager um = new NotificationManager(notificationOptions,logMan);
        assertTrue(um.configure());
        assertTrue(um.start());
    }

    /**
     * Tests that the service was correctly installed
     */
    @Test
    public void serviceInstallTest() {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("src/resources/yajsw-beta-12.05/bin/installDaemon.sh");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

        while(true) {
            String line = null;
            try {
                line = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(line == null)
                break;
            else
                System.out.println(line);
        }
        int ret = p.exitValue();
        assertEquals(0,ret);
    }

    /**
     * Tests that the service start was correctly started
     */
    @Test
    public void serviceStartTest(){
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("src/resources/yajsw-beta-12.05/bin/startDaemon.sh");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

        while(true) {
            String line = null;
            try {
                line = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(line == null)
                break;
            else
                System.out.println(line);
        }
        int ret = p.exitValue();
        assertEquals(0,ret);
    }

    /**
     * Tests that the service was correctly stoped
     */
    @Test
    public void serviceStopTest() {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("src/resources/yajsw-beta-12.05/bin/stopDaemon.sh");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

        while(true) {
            String line = null;
            try {
                line = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(line == null)
                break;
            else
                System.out.println(line);
        }
        int ret = p.exitValue();
        assertEquals(0,ret);
    }

    /**
     * Tests that the service was correctly crashed
     */
    @Test
    public void serviceCrashTest() {
        //TODO
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("src/resources/yajsw-beta-12.05/bin/stopDaemon.sh");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int ret = p.exitValue();
        assertEquals(0,ret);
    }

    /**
     * Tests that the a select From/Where statement was executed successfully
     */
    @Test
    public void selectFromWhereKQLTest() {
        StringBuilder output2 = new StringBuilder();

        String bodyargument = "SELECT \\ ALL*{protocol,portnumber,status,id,ip_address} \\ from \\ ALL/{protocol,portnumber,status,id,ip_address} \\ where \\ ALL*status \\=\"Failed\" or \\ ALL*status \\=\"Accepted\"";
        URL url = null;
        try {
            url = new URL("http://localhost:9200/_kql?kql="+ URLEncoder.encode( bodyargument, "UTF-8"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        BufferedReader rd = null;
        try {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line;
        try {
            while ((line = rd.readLine()) != null) {
                output2.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            assertEquals(200,conn.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests that the a send data statement was executed successfully
     */
    @Test
    public void sendDataTest() {
        String path = "../Platform_Module/src/resources/testconfig.json";
        String path2 = "../Platform_Module/Results.log";
        Parser hi = new Parser(path);
        hi.extractOptions();
        DataRetrievalOptions dude = hi.getDataRetrievalOptions();
        DataRetrievalManager mng = new DataRetrievalManager(dude, Logger.getLogger("NadsLogger"));
        mng.start();
        Caller c = new Caller("dude", "senddata");
        c.start();
        while (c.isAlive()) {}
        mng.stop();
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(path2), Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String test = lines.get(lines.size()-1);
        assertEquals("Anomaly detected: {hyuouho}",test);

    }

    /**
     * Tests that the a get data statement was executed successfully
     */
    @Test
    public void getDataTest() {
        String path = "/home/pedro/Documents/git/nads/Platform_Module/src/resources/testconfig.json";
        Parser hi = new Parser(path);
        hi.extractOptions();
        DataRetrievalOptions dude = hi.getDataRetrievalOptions();
        DataRetrievalManager mng = new DataRetrievalManager(dude, Logger.getLogger("NadsLogger"));
        mng.start();
        Caller c = new Caller("dude", "getdata");
        c.start();
        while (c.isAlive()) {}
        boolean results = c.getResultResponse().contains("filebeat");
        int resultCode = c.getResultCode();
        mng.stop();

        assertTrue(results);
        assertEquals(200,resultCode);
    }
}


/**
 * Executes all test
 */
class Caller implements Runnable
{
    private String threadName = "";
    private Thread t;
    private String requestType= "";
    private String resultResponse = "";
    private int resultCode = 0;

    /**
     * Gets the test result.
     * @return string, test result state response
     */
    public String getResultResponse()
    {
        return this.resultResponse;
    }

    /**
     * Gets the test result code.
     * @return int, test result state code
     */
    public int getResultCode()
    {
        return this.resultCode;
    }

    Caller( String name, String rN)
    {
        this.threadName = name;
        this.requestType = rN;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        System.out.println("Running " +  threadName );
        try {
            if(requestType.equals("senddata"))
            {
                URL url = new URL("http://localhost:8002/senddata");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                StringBuilder result = new StringBuilder();
                result.append(URLEncoder.encode("hyuouho", "UTF-8"));
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(result.toString());
                writer.flush();
                writer.close();
                os.close();

                InputStream response = conn.getInputStream();
                System.out.println(conn.getResponseCode());
            }
            else
            {
                URL url = new URL("http://localhost:8002/getdata");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                StringBuilder result = new StringBuilder();
                result.append("SELECT \\ ALL*{protocol,portnumber,status,id,ip_address} \\ from \\ ALL/{protocol,portnumber,status,id,ip_address} \\ ");
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(result.toString());
                writer.flush();
                writer.close();
                os.close();
                InputStream response = conn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                this.resultResponse = rd.readLine();
                this.resultCode = conn.getResponseCode();

            }

        }
        catch (IOException e)
        {
            System.out.println("Thread " +  threadName + " ioexception.");
        }
        System.out.println("Thread " +  threadName + " exiting.");
    }

    /**
     * Starts test thread.
     */
    public void start()
    {
        System.out.println("Starting " +  threadName );
        if (t == null)
        {
            t = new Thread (this, threadName);
            t.start ();
        }
    }

    /**
     * Stops test thread.
     */
    public void stop()
    {
        this.t.interrupt();
    }

    /**
     * Verifies if test thread is alive.
     * @return boolean, true if exists, false otherwise
     */
    public boolean isAlive() { return this.t.isAlive();}

}
