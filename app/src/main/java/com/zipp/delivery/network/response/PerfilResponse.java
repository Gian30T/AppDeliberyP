package com.zipp.delivery.network.response;

import com.google.gson.annotations.SerializedName;

public class PerfilResponse {
    
    @SerializedName("usuario")
    private AuthResponse.UsuarioApi usuario;
    
    @SerializedName("error")
    private String error;
    
    public AuthResponse.UsuarioApi getUsuario() { return usuario; }
    public String getError() { return error; }
}




