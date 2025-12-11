package com.zipp.delivery.network.request;

import com.google.gson.annotations.SerializedName;

public class DireccionRequest {
    
    @SerializedName("calle")
    private String calle;
    
    @SerializedName("ciudad")
    private String ciudad;
    
    @SerializedName("provincia")
    private String provincia;
    
    @SerializedName("codigo_postal")
    private String codigoPostal;
    
    @SerializedName("es_principal")
    private boolean esPrincipal;
    
    public DireccionRequest(String calle, String ciudad, String provincia, 
                           String codigoPostal, boolean esPrincipal) {
        this.calle = calle;
        this.ciudad = ciudad;
        this.provincia = provincia;
        this.codigoPostal = codigoPostal;
        this.esPrincipal = esPrincipal;
    }
    
    // Getters y Setters
    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }
    
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
    
    public boolean isEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(boolean esPrincipal) { this.esPrincipal = esPrincipal; }
}




