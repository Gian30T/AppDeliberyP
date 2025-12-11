package com.zipp.delivery.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductosResponse {
    
    @SerializedName("productos")
    private List<ProductoApi> productos;
    
    @SerializedName("error")
    private String error;
    
    public List<ProductoApi> getProductos() { return productos; }
    public String getError() { return error; }
    
    public static class ProductoApi {
        @SerializedName("id")
        private long id;
        
        @SerializedName("categoria_id")
        private int categoriaId;
        
        @SerializedName("nombre")
        private String nombre;
        
        @SerializedName("descripcion")
        private String descripcion;
        
        @SerializedName("precio")
        private double precio;
        
        @SerializedName("disponible")
        private int disponibleInt; // MySQL devuelve 0 o 1
        
        // Getter que convierte int a boolean
        public boolean isDisponible() {
            return disponibleInt == 1;
        }
        
        // Setter para compatibilidad
        public void setDisponible(int disponibleInt) {
            this.disponibleInt = disponibleInt;
        }
        
        // Getter del int (para debugging)
        public int getDisponibleInt() {
            return disponibleInt;
        }
        
        @SerializedName("url_imagen")
        private String urlImagen;
        
        @SerializedName("categoria_nombre")
        private String categoriaNombre;
        
        @SerializedName("opciones")
        private List<OpcionApi> opciones;
        
        public long getId() { return id; }
        public int getCategoriaId() { return categoriaId; }
        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }
        public double getPrecio() { return precio; }
        public String getUrlImagen() { return urlImagen; }
        public String getCategoriaNombre() { return categoriaNombre; }
        public List<OpcionApi> getOpciones() { return opciones; }
        
        public String getFormattedPrice() {
            return String.format("S/ %.2f", precio);
        }
    }
    
    public static class OpcionApi {
        @SerializedName("id")
        private long id;
        
        @SerializedName("nombre")
        private String nombre;
        
        @SerializedName("valores")
        private List<ValorOpcionApi> valores;
        
        public long getId() { return id; }
        public String getNombre() { return nombre; }
        public List<ValorOpcionApi> getValores() { return valores; }
    }
    
    public static class ValorOpcionApi {
        @SerializedName("id")
        private long id;
        
        @SerializedName("valor")
        private String valor;
        
        @SerializedName("precio_extra")
        private double precioExtra;
        
        public long getId() { return id; }
        public String getValor() { return valor; }
        public double getPrecioExtra() { return precioExtra; }
    }
}

