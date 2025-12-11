package com.zipp.delivery.network.request;

import com.google.gson.annotations.SerializedName;

public class CambiarContrasenaRequest {
    
    @SerializedName("contrasena_actual")
    private String contrasenaActual;
    
    @SerializedName("nueva_contrasena")
    private String nuevaContrasena;
    
    public CambiarContrasenaRequest(String contrasenaActual, String nuevaContrasena) {
        this.contrasenaActual = contrasenaActual;
        this.nuevaContrasena = nuevaContrasena;
    }
    
    public String getContrasenaActual() { return contrasenaActual; }
    public void setContrasenaActual(String contrasenaActual) { this.contrasenaActual = contrasenaActual; }
    
    public String getNuevaContrasena() { return nuevaContrasena; }
    public void setNuevaContrasena(String nuevaContrasena) { this.nuevaContrasena = nuevaContrasena; }
}




