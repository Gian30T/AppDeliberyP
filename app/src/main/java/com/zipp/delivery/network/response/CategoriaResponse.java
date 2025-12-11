package com.zipp.delivery.network.response;

import com.google.gson.annotations.SerializedName;

public class CategoriaResponse {
    
    @SerializedName("categoria")
    private CategoriasResponse.CategoriaApi categoria;
    
    @SerializedName("error")
    private String error;
    
    public CategoriasResponse.CategoriaApi getCategoria() { return categoria; }
    public String getError() { return error; }
}




