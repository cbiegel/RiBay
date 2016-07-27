package com.ribay.server.material;

import java.io.Serializable;

/**
 * Created by CD on 25.06.2016.
 */
public class Address implements Serializable {

    private String name;
    private String additionalInfo;
    private String street;
    private String zipCode;
    private String place;
    private String country;

    public Address() {
    }

    public Address(String name, String additionalInfo, String street, String zipCode, String place, String country) {
        this.name = name;
        this.additionalInfo = additionalInfo;
        this.street = street;
        this.zipCode = zipCode;
        this.place = place;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
