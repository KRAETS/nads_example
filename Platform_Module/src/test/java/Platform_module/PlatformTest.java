package Platform_module;

import org.junit.Test;
import static org.junit.Assert.*;
import parsing.Parser;

import java.util.HashMap;
import java.util.Map;

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

    //Method tests that the algorithm options were correctly parsed
    @Test
    public void algorithmsOptionsParsingTest()
    {
        String configFile = "/home/dude/Documents/capstone/project/nads/Platform_Module/src/resources/config.json";
        Parser p = new Parser(configFile);
        p.extractOptions();
        Map<String,String> m = new HashMap<String, String>();
        m.put("folder","containingFolder");
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

}
