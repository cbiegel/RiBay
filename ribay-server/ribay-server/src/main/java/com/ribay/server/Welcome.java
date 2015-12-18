package com.ribay.server;

public class Welcome
{

    private String language;
    private String greeting;

    public Welcome()
    {
    }

    public Welcome(String language, String greeting)
    {
        this.language = language;
        this.greeting = greeting;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getGreeting()
    {
        return greeting;
    }

    public void setGreeting(String greeting)
    {
        this.greeting = greeting;
    }

}
