package parsing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dude on 3/17/16.
 */
public class Option {
    public enum OptionType {STRINGLIST, INDIVIDUAL, OPTIONLIST}
    private List<String> optionStringList = new ArrayList<String>();
    private String optionStringIndividual = "";
    private List<Option> optionList = new ArrayList<Option>();
    private String optionName = "";
    private OptionType optionType;

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

    public String getOptionType()
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
    public Option(String name,List<String> ls,OptionType type)
    {
        this.optionName = name;
        this.optionType = type;
        this.optionStringList =ls;
    }
    public Option(String name,List<Option> lo, OptionType type)
    {
        this.optionName = name;
        this.optionType = type;
        this.optionList = lo;
    }
}


}
