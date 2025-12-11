package com.zipp.delivery.network.response;

import com.google.gson.annotations.SerializedName;

public class ProductoResponse {
    
    @SerializedName("producto")
    private ProductosResponse.ProductoApi producto;
    
    @SerializedName("error")
    private String error;
    
    public ProductosResponse.ProductoApi getProducto() { return producto; }
    public String getError() { return error; }
}




