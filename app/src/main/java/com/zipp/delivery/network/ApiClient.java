package com.zipp.delivery.network;

import android.content.Context;

import com.zipp.delivery.network.api.AuthApi;
import com.zipp.delivery.network.api.CategoriasApi;
import com.zipp.delivery.network.api.DireccionesApi;
import com.zipp.delivery.network.api.PedidosApi;
import com.zipp.delivery.network.api.ProductosApi;
import com.zipp.delivery.network.api.UsuariosApi;
import com.zipp.delivery.utils.SessionManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cliente API singleton para manejar todas las conexiones con el backend
 */
public class ApiClient {
    
    // URL base de la API - Cambiar según el entorno
    // Para emulador Android: usar 10.0.2.2 en lugar de localhost
    // Para dispositivo físico: usar la IP de tu computadora
    // IP detectada: 192.168.100.17
    private static final String BASE_URL = "http://192.168.100.17:3000/api/";
    
    private static ApiClient instance;
    private final Retrofit retrofit;
    private final Retrofit retrofitWithAuth;
    private SessionManager sessionManager;
    
    // APIs
    private AuthApi authApi;
    private CategoriasApi categoriasApi;
    private ProductosApi productosApi;
    private PedidosApi pedidosApi;
    private DireccionesApi direccionesApi;
    private UsuariosApi usuariosApi;
    
    private ApiClient(Context context) {
        this.sessionManager = new SessionManager(context);
        
        // Interceptor para logging (solo en debug)
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // Cliente HTTP básico (sin autenticación)
        OkHttpClient basicClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        
        // Cliente HTTP con autenticación JWT
        OkHttpClient authClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    String token = sessionManager.getToken();
                    
                    if (token != null && !token.isEmpty()) {
                        Request request = original.newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .header("Content-Type", "application/json")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    }
                    
                    return chain.proceed(original);
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        
        // Retrofit sin autenticación (para login/registro)
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(basicClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        // Retrofit con autenticación (para endpoints protegidos)
        retrofitWithAuth = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(authClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    
    /**
     * Obtener instancia del cliente API
     */
    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Reinicializar el cliente (útil después de logout)
     */
    public static synchronized void resetInstance() {
        instance = null;
    }
    
    // ========== GETTERS DE APIs ==========
    
    public AuthApi getAuthApi() {
        if (authApi == null) {
            authApi = retrofit.create(AuthApi.class);
        }
        return authApi;
    }
    
    public CategoriasApi getCategoriasApi() {
        if (categoriasApi == null) {
            categoriasApi = retrofit.create(CategoriasApi.class);
        }
        return categoriasApi;
    }
    
    public ProductosApi getProductosApi() {
        if (productosApi == null) {
            productosApi = retrofit.create(ProductosApi.class);
        }
        return productosApi;
    }
    
    public PedidosApi getPedidosApi() {
        if (pedidosApi == null) {
            pedidosApi = retrofitWithAuth.create(PedidosApi.class);
        }
        return pedidosApi;
    }
    
    public DireccionesApi getDireccionesApi() {
        if (direccionesApi == null) {
            direccionesApi = retrofitWithAuth.create(DireccionesApi.class);
        }
        return direccionesApi;
    }
    
    public UsuariosApi getUsuariosApi() {
        if (usuariosApi == null) {
            usuariosApi = retrofitWithAuth.create(UsuariosApi.class);
        }
        return usuariosApi;
    }
    
    /**
     * Obtener API de Auth con token (para endpoints protegidos como perfil)
     */
    public AuthApi getAuthApiWithToken() {
        return retrofitWithAuth.create(AuthApi.class);
    }
}

