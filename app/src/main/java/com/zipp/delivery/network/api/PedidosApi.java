package com.zipp.delivery.network.api;

import com.zipp.delivery.network.request.PedidoRequest;
import com.zipp.delivery.network.response.PedidoResponse;
import com.zipp.delivery.network.response.PedidosResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * API de pedidos
 */
public interface PedidosApi {
    
    @POST("pedidos")
    Call<PedidoResponse> crearPedido(@Body PedidoRequest request);
    
    @GET("pedidos/mis-pedidos")
    Call<PedidosResponse> obtenerMisPedidos();
    
    @GET("pedidos/{id}")
    Call<PedidoResponse> obtenerPorId(@Path("id") long id);
    
    @POST("pedidos/{id}/cancelar")
    Call<Void> cancelarPedido(@Path("id") long id);
}




