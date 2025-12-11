package com.zipp.delivery.models;

import java.io.Serializable;

public class Address implements Serializable {
    private int id;
    private String label;
    private String street;
    private String city;
    private String postalCode;
    private String instructions;
    private boolean isDefault;

    public Address() {}

    public Address(int id, String label, String street, String city, String postalCode) {
        this.id = id;
        this.label = label;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.isDefault = false;
    }

    // Getters
    public int getId() { return id; }
    public String getLabel() { return label; }
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getPostalCode() { return postalCode; }
    public String getInstructions() { return instructions; }
    public boolean isDefault() { return isDefault; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setLabel(String label) { this.label = label; }
    public void setStreet(String street) { this.street = street; }
    public void setCity(String city) { this.city = city; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public String getFullAddress() {
        return street + ", " + city + " " + postalCode;
    }

    public String getShortAddress() {
        return street + ", " + city;
    }
}




