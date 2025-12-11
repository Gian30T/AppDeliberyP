package com.zipp.delivery.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DireccionesResponse {
    
    @SerializedName("direcciones")
    private List<DireccionApi> direcciones;
    
    @SerializedName("error")
    private String error;
    
    public List<DireccionApi> getDirecciones() { return direcciones; }
    public String getError() { return error; }
    
    public static class DireccionApi {
        @SerializedName("id")
        private long id;
        
        @SerializedName("usuario_id")
        private long usuarioId;
        
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
        
        public long getId() { return id; }
        public long getUsuarioId() { return usuarioId; }
        public String getCalle() { return calle; }
        public String getCiudad() { return ciudad; }
        public String getProvincia() { return provincia; }
        public String getCodigoPostal() { return codigoPostal; }
        public boolean isEsPrincipal() { return esPrincipal; }
        
        public String getDireccionCompleta() {
            StringBuilder sb = new StringBuilder(calle);
            if (ciudad != null && !ciudad.isEmpty()) {
                sb.append(", ").append(ciudad);
            }
            if (provincia != null && !provincia.isEmpty()) {
                sb.append(", ").append(provincia);
            }
            if (codigoPostal != null && !codigoPostal.isEmpty()) {
                sb.append(" ").append(codigoPostal);
            }
            return sb.toString();
        }
    }
}




