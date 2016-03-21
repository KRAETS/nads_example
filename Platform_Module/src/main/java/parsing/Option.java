package parsing;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


/**
 * Created by dude on 3/17/16.
 */
public class Option {
    public enum OptionType {STRINGLIST, INDIVIDUAL, OPTIONLIST, OPTIONMAP}
    private List<String> optionStringList = new ArrayList<String>();
    private String optionStringIndividual = "";
    private List<Option> optionList = new ArrayList<Option>();
    private String optionName = "";
    private OptionType optionType;
    private Map<String, String> optionMap = new HashMap<String, String>();

    public void setOptionStringList(List<String> optionStringList)
    {
        this.optionStringList = optionStringList;
    }

    public void setOptionStringIndividual(String optionStringIndividual)
    {
        this.optionStringIndividual = optionStringIndividual;
    }

    public void setOptionList(List<Option> optionList)
    {
        this.optionList = optionList;
    }

    public void setOptionName(String optionName)
    {
        this.optionName = optionName;
    }

    public void setOptionMap (Map<String,String> map)
    {
        this.optionMap = map;
    }

    public void setOptionType (OptionType type)
    {
        this.optionType = type;
    }

    public boolean addToOptionList (Option o)
    {
        return this.optionList.add(o);
    }
    public boolean addToOptionStringList (String s)
    {
        return this.optionStringList.add(s);
    }

    public List<String> getOptionStringList()
    {
        return this.optionStringList;
    }

    public String getOptionStringIndividual()
    {
        return this.optionStringIndividual;
    }

    public String getOptionName()
    {
        return this.optionName;
    }

    public Map<String,String > getOptionMap()
    {
        return this.optionMap;
    }

    public OptionType getOptionType()
    {
        return this.optionType;
    }

    public List<Option> getOptionList()
    {
        return this.optionList;
    }

    public Option(String name,String s,OptionType type)
    {
        this.optionName = name;
        this.optionType = type;
        this.optionStringIndividual = s;
    }
    public Option(String name,OptionType type)
    {

        this.optionName = name;
        this.optionType = type;

    }
    public Option(String name, List<Option> lo, OptionType type)
    {
        this.optionName = name;
        this.optionType = type;
        this.optionList = lo;
    }
}
