package com.zipp.delivery.network.api;

import com.zipp.delivery.network.response.CategoriaResponse;
import com.zipp.delivery.network.response.CategoriasResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * API de categorías
 */
public interface CategoriasApi {
    
    @GET("categorias")
    Call<CategoriasResponse> obtenerTodas();
    
    @GET("categorias/con-productos")
    Call<CategoriasResponse> obtenerConProductos();
    
    @GET("categorias/{id}")
    Call<CategoriaResponse> obtenerPorId(@Path("id") int id);
}




