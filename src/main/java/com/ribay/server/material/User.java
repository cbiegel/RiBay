package com.ribay.server.material;

import java.util.UUID;

import com.basho.riak.client.api.annotations.RiakIndex;

public class User
{
    private UUID uuid;
    @RiakIndex(name = "emailAddress")
    private String emailAddress;
    private String password;
    private String name;

    public User(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public User(String emailAddress, String password, UUID uuid, String name)
    {
        this.emailAddress = emailAddress;
        this.password = password;
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public void setUuid(UUID uuid)
    {
        this.uuid = uuid;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

}
