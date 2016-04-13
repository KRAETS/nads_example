package parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by dude on 3/17/16.
 */
public class Option {
    /**
     * The enum Option type.
     */
    public enum OptionType {
        /**
         * Stringlist option type.
         */
        STRINGLIST, /**
         * Individual option type.
         */
        INDIVIDUAL, /**
         * Optionlist option type.
         */
        OPTIONLIST, /**
         * Optionmap option type.
         */
        OPTIONMAP}
    private List<String> optionStringList = new ArrayList<String>();
    private String optionStringIndividual = "";
    private List<Option> optionList = new ArrayList<Option>();
    private String optionName = "";
    private OptionType optionType;
    private Map<String, String> optionMap = new HashMap<String, String>();


    /**
     * Set option string list.
     *
     * @param optionStringList the option string list
     */
    public void setOptionStringList(List<String> optionStringList){
        this.optionStringList = optionStringList;
    }

    /**
     * Set option string individual.
     *
     * @param optionStringIndividual the option string individual
     */
    public void setOptionStringIndividual(String optionStringIndividual){
        this.optionStringIndividual = optionStringIndividual;
    }

    /**
     * Set option list.
     *
     * @param optionList the option list
     */
    public void setOptionList(List<Option> optionList){
        this.optionList = optionList;
    }

    /**
     * Set option name.
     *
     * @param optionName the option name
     */
    public void setOptionName(String optionName){
        this.optionName = optionName;
    }

    /**
     * Set option map.
     *
     * @param map the map
     */
    public void setOptionMap (Map<String,String> map){
        this.optionMap = map;
    }

    /**
     * Set option type.
     *
     * @param type the type
     */
    public void setOptionType (OptionType type){
        this.optionType = type;
    }

    /**
     * Add to option list boolean.
     *
     * @param o the o
     * @return the boolean
     */
    public boolean addToOptionList (Option o){
        return this.optionList.add(o);
    }

    /**
     * Add to option string list boolean.
     *
     * @param s the s
     * @return the boolean
     */
    public boolean addToOptionStringList (String s){
        return this.optionStringList.add(s);
    }

    /**
     * Get option string list list.
     *
     * @return the list
     */
    public List<String> getOptionStringList(){
        return this.optionStringList;
    }

    /**
     * Get option string individual string.
     *
     * @return the string
     */
    public String getOptionStringIndividual(){
        return this.optionStringIndividual;
    }

    /**
     * Get option name string.
     *
     * @return the string
     */
    public String getOptionName(){
        return this.optionName;
    }

    /**
     * Get option map map.
     *
     * @return the map
     */
    public Map<String,String > getOptionMap(){
        return this.optionMap;
    }

    /**
     * Get option type option type.
     *
     * @return the option type
     */
    public OptionType getOptionType(){
        return this.optionType;
    }

    /**
     * Get option list list.
     *
     * @return the list
     */
    public List<Option> getOptionList(){
        return this.optionList;
    }

    /**
     * Instantiates a new Option.
     *
     * @param name the name
     * @param s    the s
     * @param type the type
     */
    public Option(String name,String s,OptionType type){
        this.optionName = name;
        this.optionType = type;
        this.optionStringIndividual = s;
    }

    /**
     * Instantiates a new Option.
     *
     * @param name the name
     * @param type the type
     */
    public Option(String name,OptionType type){
        this.optionName = name;
        this.optionType = type;
    }

    /**
     * Instantiates a new Option.
     *
     * @param name the name
     * @param lo   the lo
     * @param type the type
     */
    public Option(String name, List<Option> lo, OptionType type){
        this.optionName = name;
        this.optionType = type;
        this.optionList = lo;
    }
}
