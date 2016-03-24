package Platform_module;

import algorithms.AlgorithmManager;
import org.junit.Test;
import static org.junit.Assert.*;
import parsing.Parser;
import platform.PlatformManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by dude on 3/22/16.
 */
public class PlatformTest {

    //Method tests that the platform options were correctly parsed
    @Test
    public void platformOptionsParsingTest()
    {
        String configFile = "/home/dude/Documents/capstone/project/nads/Platform_Module/src/resources/config.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        assertEquals("something", p.getPlatformOptions().getWorkingDirectory());
        assertEquals("nads.log", p.getPlatformOptions().getLogLocation());
    }

    //Method tests that the data retrieval options were correctly parsed
    @Test
    public void dataRetrievalOptionsParsingTest()
    {
        String configFile = "/home/dude/Documents/capstone/project/nads/Platform_Module/src/resources/config.json";
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
        String configFile = "/home/dude/Documents/capstone/project/nads/Platform_Module/src/resources/config.json";
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
        String configFile = "/home/dude/Documents/capstone/project/nads/Platform_Module/src/resources/config.json";
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
        assertEquals(l, p.getNotificationOptions().getUsers());
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
        String configFile = "/home/dude/Documents/capstone/project/nads/Platform_Module/src/resources/config.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        Map<String,String> m = new HashMap<String, String>();
        m.put("folder","/home/dude/Documents/capstone/project/nads/algorithms/loop_detec/test.py");
        m.put("model","values");
        m.put("ips","[\"136.145.59.152\"]");
        m.put("exampleip","value");
        m.put("trap_oid","value");
        assertEquals("[testalgorithm]", p.getAlgorithmsOptions().getAlgorithmNames().toString());
        assertEquals(m, p.getAlgorithmsOptions().getAlgorithmParameters("testalgorithm"));
    }

    //Method tests that the parser detects a nonexistent json file
    @Test
    public void nonexistantJsonParsingTest()
    {
        String configFile = "/home/dude/Documents/capstone/project/nads/Platform_Module/src/resources/conerrfig.json";
        Parser p = new Parser(configFile);
        assertFalse(p.extractOptions());
    }

    //Method tests that the parser detects a incorrect json file
    @Test
    public void incorrectSyntaxJsonParsingTest()
    {
        String configFile = "/home/dude/Documents/capstone/project/nads/Platform_Module/src/resources/wrongformat.json";
        Parser p = new Parser(configFile);
        assertFalse(p.extractOptions());
    }
    //Method tests that the parser detects a syntactically correct json file but with the wrong fields
    @Test
    public void incorrectFieldsJsonParsingTest()
    {
        String configFile = "/home/dude/Documents/capstone/project/nads/Platform_Module/src/resources/correctsyntaxwrongfields.json";
        Parser p = new Parser(configFile);
        assertFalse(p.extractOptions());
    }

    //Method tests that the platform manager was correctly initialized
    @Test
    public void platformInitializingTest()
    {
        String configFile = "/home/dude/Documents/capstone/project/nads/Platform_Module/src/resources/config.json";
        PlatformManager pm = new PlatformManager(configFile, null);
        assertTrue(pm.configure());
        assertTrue(pm.start());
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
        String configFile = "/home/dude/Documents/capstone/project/nads/Platform_Module/src/resources/config.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        AlgorithmManager am  = new AlgorithmManager(p.getAlgorithmsOptions(), logMan);
        assertTrue(am.configure());
        assertTrue(am.start());
    }



}