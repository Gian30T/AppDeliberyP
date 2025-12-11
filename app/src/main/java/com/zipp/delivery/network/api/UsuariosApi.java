package com.zipp.delivery.network.api;

import com.zipp.delivery.network.request.ActualizarPerfilRequest;
import com.zipp.delivery.network.response.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * API de usuarios
 */
public interface UsuariosApi {
    
    @GET("usuarios/{id}")
    Call<AuthResponse> obtenerUsuario(@Path("id") long id);
    
    @PUT("usuarios/{id}")
    Call<AuthResponse> actualizarUsuario(@Path("id") long id, @Body ActualizarPerfilRequest request);
}


