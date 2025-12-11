package com.zipp.delivery.network.response;

import com.google.gson.annotations.SerializedName;

public class PedidoResponse {
    
    @SerializedName("mensaje")
    private String mensaje;
    
    @SerializedName("pedido")
    private PedidosResponse.PedidoApi pedido;
    
    @SerializedName("error")
    private String error;
    
    public String getMensaje() { return mensaje; }
    public PedidosResponse.PedidoApi getPedido() { return pedido; }
    public String getError() { return error; }
}




