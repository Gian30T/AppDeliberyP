package com.zipp.delivery.network.request;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("contrasena")
    private String contrasena;
    
    public LoginRequest(String email, String contrasena) {
        this.email = email;
        this.contrasena = contrasena;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getContrasena() {
        return contrasena;
    }
    
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}




