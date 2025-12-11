package com.zipp.delivery.network.request;

import com.google.gson.annotations.SerializedName;

public class RegistroRequest {
    
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("apellido")
    private String apellido;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("contrasena")
    private String contrasena;
    
    @SerializedName("numero_telefono")
    private String numeroTelefono;
    
    @SerializedName("rol_usuario")
    private String rolUsuario;
    
    public RegistroRequest(String nombre, String apellido, String email, 
                          String contrasena, String numeroTelefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasena = contrasena;
        this.numeroTelefono = numeroTelefono;
        this.rolUsuario = "cliente"; // Por defecto es cliente
    }
    
    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    
    public String getNumeroTelefono() { return numeroTelefono; }
    public void setNumeroTelefono(String numeroTelefono) { this.numeroTelefono = numeroTelefono; }
    
    public String getRolUsuario() { return rolUsuario; }
    public void setRolUsuario(String rolUsuario) { this.rolUsuario = rolUsuario; }
}




