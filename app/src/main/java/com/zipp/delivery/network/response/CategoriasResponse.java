package com.zipp.delivery.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoriasResponse {
    
    @SerializedName("categorias")
    private List<CategoriaApi> categorias;
    
    @SerializedName("error")
    private String error;
    
    public List<CategoriaApi> getCategorias() { return categorias; }
    public String getError() { return error; }
    
    public static class CategoriaApi {
        @SerializedName("id")
        private int id;
        
        @SerializedName("nombre")
        private String nombre;
        
        @SerializedName("descripcion")
        private String descripcion;
        
        @SerializedName("productos")
        private List<ProductosResponse.ProductoApi> productos;
        
        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }
        public List<ProductosResponse.ProductoApi> getProductos() { return productos; }
    }
}




