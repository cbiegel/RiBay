package com.ribay.server.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ribay.server.material.Cart;
import com.ribay.server.material.User;

import java.io.Serializable;

/**
 * Created by Christian on 09.02.2016.
 */
public class SessionData implements Serializable {

    private String sessionId;
    private User user;
    private String hash;

    @JsonIgnore
    private transient boolean isNew; // transient so no serialization to json and no serialization for generation of hash

    public SessionData() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @JsonIgnore
    public boolean isNew() {
        return isNew;
    }

    @JsonProperty
    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SessionData that = (SessionData) o;

        if (sessionId != null ? !sessionId.equals(that.sessionId) : that.sessionId != null) return false;
        return user != null ? user.equals(that.user) : that.user == null;

    }

    @Override
    public int hashCode() {
        int result = sessionId != null ? sessionId.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

}
