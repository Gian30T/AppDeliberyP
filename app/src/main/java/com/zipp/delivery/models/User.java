package com.zipp.delivery.models;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String profileImageUrl;
    private List<Address> addresses;
    private Address defaultAddress;

    public User() {}

    public User(int id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public List<Address> getAddresses() { return addresses; }
    public Address getDefaultAddress() { return defaultAddress; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }
    public void setDefaultAddress(Address defaultAddress) { this.defaultAddress = defaultAddress; }

    public String getFirstName() {
        if (name != null && !name.isEmpty()) {
            String[] parts = name.split(" ");
            return parts[0];
        }
        return "";
    }

    public String getInitials() {
        if (name != null && !name.isEmpty()) {
            String[] parts = name.split(" ");
            if (parts.length >= 2) {
                return String.valueOf(parts[0].charAt(0)) + parts[1].charAt(0);
            }
            return String.valueOf(name.charAt(0));
        }
        return "U";
    }
}




