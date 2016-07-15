package com.yz.restful.DFSConfig;


/**
* name(key) and value pair model
* @author Happy Fish / YuQing
* @version Version 1.0
*/
public class NameValuePair
{
    protected String name;
    protected String value;

    public NameValuePair()
    {
    }

    public NameValuePair(String name)
    {
        this.name = name;
    }

    public NameValuePair(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return this.name;
    }

    public String getValue()
    {
        return this.value;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
