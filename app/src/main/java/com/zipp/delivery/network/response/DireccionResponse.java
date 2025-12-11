package com.zipp.delivery.network.response;

import com.google.gson.annotations.SerializedName;

public class DireccionResponse {
    
    @SerializedName("mensaje")
    private String mensaje;
    
    @SerializedName("direccion")
    private DireccionesResponse.DireccionApi direccion;
    
    @SerializedName("error")
    private String error;
    
    public String getMensaje() { return mensaje; }
    public DireccionesResponse.DireccionApi getDireccion() { return direccion; }
    public String getError() { return error; }
}




