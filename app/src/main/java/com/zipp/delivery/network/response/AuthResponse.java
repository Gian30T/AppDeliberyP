package com.zipp.delivery.network.response;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    
    @SerializedName("mensaje")
    private String mensaje;
    
    @SerializedName("usuario")
    private UsuarioApi usuario;
    
    @SerializedName("token")
    private String token;
    
    @SerializedName("error")
    private String error;
    
    public String getMensaje() { return mensaje; }
    public UsuarioApi getUsuario() { return usuario; }
    public String getToken() { return token; }
    public String getError() { return error; }
    
    public boolean isSuccess() {
        return error == null && token != null;
    }
    
    // Clase interna para el usuario de la API
    public static class UsuarioApi {
        @SerializedName("id")
        private long id;
        
        @SerializedName("nombre")
        private String nombre;
        
        @SerializedName("apellido")
        private String apellido;
        
        @SerializedName("email")
        private String email;
        
        @SerializedName("numero_telefono")
        private String numeroTelefono;
        
        @SerializedName("rol_usuario")
        private String rolUsuario;
        
        public long getId() { return id; }
        public String getNombre() { return nombre; }
        public String getApellido() { return apellido; }
        public String getEmail() { return email; }
        public String getNumeroTelefono() { return numeroTelefono; }
        public String getRolUsuario() { return rolUsuario; }
        
        public String getNombreCompleto() {
            if (apellido != null && !apellido.isEmpty()) {
                return nombre + " " + apellido;
            }
            return nombre;
        }
    }
}




