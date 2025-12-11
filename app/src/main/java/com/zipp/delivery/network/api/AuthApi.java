package com.zipp.delivery.network.api;

import com.zipp.delivery.network.request.CambiarContrasenaRequest;
import com.zipp.delivery.network.request.LoginRequest;
import com.zipp.delivery.network.request.RegistroRequest;
import com.zipp.delivery.network.response.AuthResponse;
import com.zipp.delivery.network.response.PerfilResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * API de autenticación
 */
public interface AuthApi {
    
    @POST("auth/registrar")
    Call<AuthResponse> registrar(@Body RegistroRequest request);
    
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);
    
    @GET("auth/perfil")
    Call<PerfilResponse> obtenerPerfil();
    
    @PUT("auth/cambiar-contrasena")
    Call<Void> cambiarContrasena(@Body CambiarContrasenaRequest request);
}




