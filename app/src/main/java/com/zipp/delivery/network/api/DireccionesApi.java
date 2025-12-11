package com.zipp.delivery.network.api;

import com.zipp.delivery.network.request.DireccionRequest;
import com.zipp.delivery.network.response.DireccionResponse;
import com.zipp.delivery.network.response.DireccionesResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * API de direcciones
 */
public interface DireccionesApi {
    
    @GET("direcciones/mis-direcciones")
    Call<DireccionesResponse> obtenerMisDirecciones();
    
    @POST("direcciones")
    Call<DireccionResponse> crear(@Body DireccionRequest request);
    
    @GET("direcciones/{id}")
    Call<DireccionResponse> obtenerPorId(@Path("id") long id);
    
    @PUT("direcciones/{id}")
    Call<DireccionResponse> actualizar(@Path("id") long id, @Body DireccionRequest request);
    
    @PUT("direcciones/{id}/principal")
    Call<Void> establecerComoPrincipal(@Path("id") long id);
    
    @DELETE("direcciones/{id}")
    Call<Void> eliminar(@Path("id") long id);
}




