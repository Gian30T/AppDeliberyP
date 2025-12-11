package com.zipp.delivery.network.api;

import com.zipp.delivery.network.response.ProductoResponse;
import com.zipp.delivery.network.response.ProductosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * API de productos
 */
public interface ProductosApi {
    
    @GET("productos/disponibles")
    Call<ProductosResponse> obtenerDisponibles();
    
    @GET("productos/buscar")
    Call<ProductosResponse> buscar(@Query("q") String query);
    
    @GET("productos/categoria/{categoria_id}")
    Call<ProductosResponse> obtenerPorCategoria(@Path("categoria_id") int categoriaId);
    
    @GET("productos/{id}")
    Call<ProductoResponse> obtenerPorId(@Path("id") long id);
    
    @GET("productos/{id}/completo")
    Call<ProductoResponse> obtenerConOpciones(@Path("id") long id);
}




