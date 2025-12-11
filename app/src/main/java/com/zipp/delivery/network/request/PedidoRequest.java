package com.zipp.delivery.network.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PedidoRequest {
    
    @SerializedName("direccion_entrega_id")
    private long direccionEntregaId;
    
    @SerializedName("monto_total")
    private double montoTotal;
    
    @SerializedName("costo_envio")
    private double costoEnvio;
    
    @SerializedName("metodo_pago")
    private String metodoPago;
    
    @SerializedName("items")
    private List<ItemPedidoRequest> items;
    
    @SerializedName("notas")
    private String notas;
    
    public PedidoRequest(long direccionEntregaId, double montoTotal, double costoEnvio,
                        String metodoPago, List<ItemPedidoRequest> items, String notas) {
        this.direccionEntregaId = direccionEntregaId;
        this.montoTotal = montoTotal;
        this.costoEnvio = costoEnvio;
        this.metodoPago = metodoPago;
        this.items = items;
        this.notas = notas;
    }
    
    // Getters y Setters
    public long getDireccionEntregaId() { return direccionEntregaId; }
    public void setDireccionEntregaId(long direccionEntregaId) { this.direccionEntregaId = direccionEntregaId; }
    
    public double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }
    
    public double getCostoEnvio() { return costoEnvio; }
    public void setCostoEnvio(double costoEnvio) { this.costoEnvio = costoEnvio; }
    
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    
    public List<ItemPedidoRequest> getItems() { return items; }
    public void setItems(List<ItemPedidoRequest> items) { this.items = items; }
    
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    
    // Clase interna para items del pedido
    public static class ItemPedidoRequest {
        @SerializedName("producto_id")
        private long productoId;
        
        @SerializedName("cantidad")
        private int cantidad;
        
        @SerializedName("precio_unitario")
        private double precioUnitario;
        
        @SerializedName("opciones")
        private List<Long> opciones;
        
        public ItemPedidoRequest(long productoId, int cantidad, double precioUnitario, List<Long> opciones) {
            this.productoId = productoId;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.opciones = opciones;
        }
        
        public long getProductoId() { return productoId; }
        public int getCantidad() { return cantidad; }
        public double getPrecioUnitario() { return precioUnitario; }
        public List<Long> getOpciones() { return opciones; }
    }
}




