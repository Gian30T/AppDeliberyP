package com.zipp.delivery.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PedidosResponse {
    
    @SerializedName("pedidos")
    private List<PedidoApi> pedidos;
    
    @SerializedName("error")
    private String error;
    
    public List<PedidoApi> getPedidos() { return pedidos; }
    public String getError() { return error; }
    
    public static class PedidoApi {
        @SerializedName("id")
        private long id;
        
        @SerializedName("usuario_id")
        private long usuarioId;
        
        @SerializedName("direccion_entrega_id")
        private long direccionEntregaId;
        
        @SerializedName("repartidor_id")
        private Long repartidorId;
        
        @SerializedName("monto_total")
        private double montoTotal;
        
        @SerializedName("costo_envio")
        private double costoEnvio;
        
        @SerializedName("estado_pedido")
        private String estadoPedido;
        
        @SerializedName("metodo_pago")
        private String metodoPago;
        
        @SerializedName("fecha_pedido")
        private String fechaPedido;
        
        @SerializedName("fecha_entrega")
        private String fechaEntrega;
        
        @SerializedName("notas")
        private String notas;
        
        @SerializedName("items")
        private List<ItemPedidoApi> items;
        
        @SerializedName("direccion")
        private DireccionesResponse.DireccionApi direccion;
        
        public long getId() { return id; }
        public long getUsuarioId() { return usuarioId; }
        public long getDireccionEntregaId() { return direccionEntregaId; }
        public Long getRepartidorId() { return repartidorId; }
        public double getMontoTotal() { return montoTotal; }
        public double getCostoEnvio() { return costoEnvio; }
        public String getEstadoPedido() { return estadoPedido; }
        public String getMetodoPago() { return metodoPago; }
        public String getFechaPedido() { return fechaPedido; }
        public String getFechaEntrega() { return fechaEntrega; }
        public String getNotas() { return notas; }
        public List<ItemPedidoApi> getItems() { return items; }
        public DireccionesResponse.DireccionApi getDireccion() { return direccion; }
        
        public String getFormattedTotal() {
            return String.format("S/ %.2f", montoTotal);
        }
        
        public String getEstadoLegible() {
            switch (estadoPedido) {
                case "pendiente": return "Pendiente";
                case "procesando": return "Procesando";
                case "listo_para_recoger": return "Listo para recoger";
                case "en_camino": return "En camino";
                case "entregado": return "Entregado";
                case "cancelado": return "Cancelado";
                default: return estadoPedido;
            }
        }
    }
    
    public static class ItemPedidoApi {
        @SerializedName("id")
        private long id;
        
        @SerializedName("producto_id")
        private long productoId;
        
        @SerializedName("cantidad")
        private int cantidad;
        
        @SerializedName("precio_unitario")
        private double precioUnitario;
        
        @SerializedName("producto_nombre")
        private String productoNombre;
        
        public long getId() { return id; }
        public long getProductoId() { return productoId; }
        public int getCantidad() { return cantidad; }
        public double getPrecioUnitario() { return precioUnitario; }
        public String getProductoNombre() { return productoNombre; }
        
        public double getSubtotal() {
            return precioUnitario * cantidad;
        }
    }
}

