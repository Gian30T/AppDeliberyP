package com.zipp.delivery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zipp.delivery.R;
import com.zipp.delivery.adapters.OrderAdapter;
import com.zipp.delivery.network.ApiClient;
import com.zipp.delivery.network.response.DireccionesResponse;
import com.zipp.delivery.network.response.PedidosResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends AppCompatActivity {

    private static final String TAG = "OrdersActivity";
    
    private ImageView ivBack;
    private TextView tvTitle;
    private RecyclerView rvOrders;
    private ProgressBar progressBar;
    private View emptyState;
    private TextView tvEmptyMessage;

    private ApiClient apiClient;
    private OrderAdapter orderAdapter;
    private List<PedidosResponse.PedidoApi> ordersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        apiClient = ApiClient.getInstance(this);
        ordersList = new ArrayList<>();

        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadOrders();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        rvOrders = findViewById(R.id.rv_orders);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);
        tvEmptyMessage = findViewById(R.id.tv_empty_message);
    }

    private void setupRecyclerView() {
        orderAdapter = new OrderAdapter(ordersList, this::onOrderClick);
        orderAdapter.setCancelOrderListener(this::onCancelOrder);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(orderAdapter);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
    }

    private void loadOrders() {
        setLoading(true);
        
        apiClient.getPedidosApi().obtenerMisPedidos().enqueue(new Callback<PedidosResponse>() {
            @Override
            public void onResponse(Call<PedidosResponse> call, Response<PedidosResponse> response) {
                setLoading(false);
                
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response isSuccessful: " + response.isSuccessful());
                
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        Log.e(TAG, "Response body is null");
                        showEmptyState("Error: respuesta vacía");
                        Toast.makeText(OrdersActivity.this, "Error: respuesta vacía del servidor", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    List<PedidosResponse.PedidoApi> pedidos = response.body().getPedidos();
                    Log.d(TAG, "Pedidos recibidos: " + (pedidos != null ? pedidos.size() : "null"));
                    
                    if (pedidos != null && !pedidos.isEmpty()) {
                        ordersList.clear();
                        ordersList.addAll(pedidos);
                        
                        // Debug: Verificar estructura de direcciones
                        for (PedidosResponse.PedidoApi p : pedidos) {
                            DireccionesResponse.DireccionApi dir = p.getDireccion();
                            Log.d(TAG, "Pedido #" + p.getId() + " - Items: " + 
                                (p.getItems() != null ? p.getItems().size() : "null") +
                                " - Dirección: " + 
                                (dir != null ? (dir.getCalle() != null ? dir.getCalle() : "sin calle") : "null"));
                        }
                        
                        orderAdapter.notifyDataSetChanged();
                        showOrders();
                        Log.d(TAG, "Pedidos cargados exitosamente: " + ordersList.size());
                    } else {
                        showEmptyState("No tienes pedidos aún");
                        Log.d(TAG, "Lista de pedidos está vacía o es null");
                    }
                } else {
                    String errorBody = null;
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
                        Log.e(TAG, "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error leyendo errorBody: " + e.getMessage());
                    }
                    
                    String error = response.body() != null ? response.body().getError() : "Error desconocido";
                    showEmptyState("Error al cargar pedidos: " + error);
                    Log.e(TAG, "Error al cargar pedidos: " + response.code() + " - " + error);
                    Log.e(TAG, "Error body completo: " + errorBody);
                    Toast.makeText(OrdersActivity.this, "Error al cargar pedidos: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PedidosResponse> call, Throwable t) {
                setLoading(false);
                showEmptyState("Error de conexión");
                Log.e(TAG, "Error de conexión: " + t.getMessage());
                Log.e(TAG, "Tipo de error: " + t.getClass().getName());
                if (t.getCause() != null) {
                    Log.e(TAG, "Causa: " + t.getCause().getMessage());
                }
                Log.e(TAG, "Stack trace:", t);
                Toast.makeText(OrdersActivity.this, "Error de conexión. Verifica tu internet.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onOrderClick(PedidosResponse.PedidoApi pedido) {
        // Mostrar detalles del pedido en un diálogo
        showOrderDetails(pedido);
    }
    
    private void onCancelOrder(PedidosResponse.PedidoApi pedido, int position) {
        new AlertDialog.Builder(this)
            .setTitle("Cancelar pedido")
            .setMessage("¿Estás seguro de que deseas cancelar el pedido #" + pedido.getId() + "?")
            .setPositiveButton("Sí, cancelar", (dialog, which) -> {
                cancelOrder(pedido, position);
            })
            .setNegativeButton("No", null)
            .show();
    }
    
    private void cancelOrder(PedidosResponse.PedidoApi pedido, int position) {
        setLoading(true);
        
        apiClient.getPedidosApi().cancelarPedido(pedido.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                setLoading(false);
                
                if (response.isSuccessful()) {
                    Toast.makeText(OrdersActivity.this, "Pedido cancelado exitosamente", Toast.LENGTH_SHORT).show();
                    // Recargar lista de pedidos
                    loadOrders();
                } else {
                    Toast.makeText(OrdersActivity.this, "Error al cancelar pedido: " + response.code(), 
                        Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al cancelar pedido: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                setLoading(false);
                Toast.makeText(OrdersActivity.this, "Error de conexión al cancelar pedido", 
                    Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error de conexión: " + t.getMessage());
            }
        });
    }
    
    private void showOrderDetails(PedidosResponse.PedidoApi pedido) {
        // Crear diálogo personalizado
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_order_details);
        
        // Configurar ventana del diálogo con fondo sólido blanco
        android.view.Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.white);
            window.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
                           android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            
            // Añadir márgenes laterales para que no toque los bordes
            android.view.WindowManager.LayoutParams params = window.getAttributes();
            params.horizontalMargin = 32f; // Margen horizontal en dp
            params.verticalMargin = 40f;   // Margen vertical en dp
            window.setAttributes(params);
        }
        
        dialog.setCancelable(true);
        
        // Obtener vistas
        TextView tvOrderId = dialog.findViewById(R.id.tv_order_id);
        TextView tvOrderStatus = dialog.findViewById(R.id.tv_order_status);
        TextView tvOrderDate = dialog.findViewById(R.id.tv_order_date);
        TextView tvPaymentMethod = dialog.findViewById(R.id.tv_payment_method);
        TextView tvAddressStreet = dialog.findViewById(R.id.tv_address_street);
        TextView tvAddressCity = dialog.findViewById(R.id.tv_address_city);
        TextView tvAddressPostal = dialog.findViewById(R.id.tv_address_postal);
        TextView tvAddressReference = dialog.findViewById(R.id.tv_address_reference);
        TextView tvSubtotal = dialog.findViewById(R.id.tv_subtotal);
        TextView tvDelivery = dialog.findViewById(R.id.tv_delivery);
        TextView tvTotal = dialog.findViewById(R.id.tv_total);
        TextView tvNotes = dialog.findViewById(R.id.tv_notes);
        LinearLayout llProducts = dialog.findViewById(R.id.ll_products);
        LinearLayout llNotes = dialog.findViewById(R.id.ll_notes);
        
        // Configurar datos básicos
        tvOrderId.setText("Pedido #" + pedido.getId());
        tvOrderStatus.setText(pedido.getEstadoLegible());
        tvOrderDate.setText("Fecha: " + formatDate(pedido.getFechaPedido()));
        
        String metodoPago = pedido.getMetodoPago() != null && pedido.getMetodoPago().equals("efectivo") ? 
                           "Efectivo" : "Tarjeta";
        tvPaymentMethod.setText("Método de pago: " + metodoPago);
        
        // Configurar dirección
        DireccionesResponse.DireccionApi direccion = pedido.getDireccion();
        Log.d(TAG, "Dirección del pedido: " + (direccion != null ? "existe" : "null"));
        
        if (direccion != null && direccion.getCalle() != null && !direccion.getCalle().isEmpty()) {
            // Calle principal
            String calle = direccion.getCalle();
            tvAddressStreet.setText(calle);
            tvAddressStreet.setVisibility(View.VISIBLE);
            Log.d(TAG, "Calle: " + calle);
            
            // Ciudad y provincia
            StringBuilder ciudadBuilder = new StringBuilder();
            String ciudad = direccion.getCiudad();
            String provincia = direccion.getProvincia();
            
            if (ciudad != null && !ciudad.isEmpty() && !ciudad.equals("Ciudad")) {
                ciudadBuilder.append(ciudad);
            }
            if (provincia != null && !provincia.isEmpty()) {
                if (ciudadBuilder.length() > 0) {
                    ciudadBuilder.append(", ");
                }
                ciudadBuilder.append(provincia);
            }
            
            if (ciudadBuilder.length() > 0) {
                tvAddressCity.setText(ciudadBuilder.toString());
                tvAddressCity.setVisibility(View.VISIBLE);
            } else {
                tvAddressCity.setVisibility(View.GONE);
            }
            
            // Código postal (solo si no es el valor por defecto)
            String codigoPostal = direccion.getCodigoPostal();
            if (codigoPostal != null && !codigoPostal.isEmpty() && !codigoPostal.equals("00000")) {
                tvAddressPostal.setText("CP: " + codigoPostal);
                tvAddressPostal.setVisibility(View.VISIBLE);
            } else {
                tvAddressPostal.setVisibility(View.GONE);
            }
            
            // Intentar extraer referencia de las notas si existe
            String notas = pedido.getNotas();
            View dividerReference = dialog.findViewById(R.id.divider_reference);
            if (notas != null && !notas.isEmpty()) {
                // Si las notas contienen "Referencia:" o similar, extraer esa parte
                String referencia = extractReferenceFromNotes(notas);
                if (referencia != null && !referencia.isEmpty()) {
                    tvAddressReference.setText("Referencia: " + referencia);
                    tvAddressReference.setVisibility(View.VISIBLE);
                    if (dividerReference != null) {
                        dividerReference.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvAddressReference.setVisibility(View.GONE);
                    if (dividerReference != null) {
                        dividerReference.setVisibility(View.GONE);
                    }
                }
            } else {
                tvAddressReference.setVisibility(View.GONE);
                if (dividerReference != null) {
                    dividerReference.setVisibility(View.GONE);
                }
            }
        } else {
            // Dirección no disponible - mostrar mensaje claro
            tvAddressStreet.setText("Dirección no disponible");
            tvAddressStreet.setTextColor(getResources().getColor(R.color.text_hint, null));
            tvAddressCity.setVisibility(View.GONE);
            tvAddressPostal.setVisibility(View.GONE);
            tvAddressReference.setVisibility(View.GONE);
            Log.w(TAG, "No se encontró dirección para el pedido #" + pedido.getId());
        }
        
        // Configurar productos
        if (pedido.getItems() != null && !pedido.getItems().isEmpty()) {
            for (PedidosResponse.ItemPedidoApi item : pedido.getItems()) {
                TextView productView = new TextView(this);
                productView.setText(String.format("• %d x %s - S/ %.2f",
                    item.getCantidad(),
                    item.getProductoNombre() != null ? item.getProductoNombre() : "Producto",
                    item.getSubtotal()));
                productView.setTextColor(getResources().getColor(R.color.text_primary, null));
                productView.setTextSize(15);
                productView.setPadding(0, 8, 0, 8);
                productView.setLineSpacing(4, 1);
                llProducts.addView(productView);
            }
        } else {
            TextView emptyView = new TextView(this);
            emptyView.setText("No hay productos en este pedido");
            emptyView.setTextColor(getResources().getColor(R.color.text_hint, null));
            emptyView.setTextSize(14);
            emptyView.setPadding(0, 8, 0, 8);
            llProducts.addView(emptyView);
        }
        
        // Configurar resumen
        double subtotal = pedido.getMontoTotal() - pedido.getCostoEnvio();
        tvSubtotal.setText(String.format("S/ %.2f", subtotal));
        tvDelivery.setText(pedido.getCostoEnvio() > 0 ? 
            String.format("S/ %.2f", pedido.getCostoEnvio()) : "Gratis");
        tvTotal.setText(pedido.getFormattedTotal());
        
        // Configurar notas (excluyendo la referencia si ya se mostró)
        String notas = pedido.getNotas();
        if (notas != null && !notas.isEmpty()) {
            String referencia = extractReferenceFromNotes(notas);
            String notasLimpias = notas;
            
            // Si hay referencia, removerla de las notas para no duplicar
            if (referencia != null && !referencia.isEmpty()) {
                notasLimpias = notas.replace("Referencia: " + referencia, "").trim();
                notasLimpias = notasLimpias.replace("referencia: " + referencia, "").trim();
                notasLimpias = notasLimpias.replace(referencia, "").trim();
            }
            
            // Solo mostrar notas si hay algo más además de la referencia
            if (!notasLimpias.isEmpty() && !notasLimpias.equals(notas)) {
                tvNotes.setText(notasLimpias);
                llNotes.setVisibility(View.VISIBLE);
            } else if (referencia == null || referencia.isEmpty()) {
                // Si no hay referencia, mostrar todas las notas
                tvNotes.setText(notas);
                llNotes.setVisibility(View.VISIBLE);
            } else {
                llNotes.setVisibility(View.GONE);
            }
        } else {
            llNotes.setVisibility(View.GONE);
        }
        
        // Botón cerrar
        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    private String extractReferenceFromNotes(String notas) {
        if (notas == null || notas.isEmpty()) {
            return null;
        }
        
        // Buscar patrones como "Referencia:", "referencia:", "Ref:", etc.
        String[] patterns = {
            "Referencia:",
            "referencia:",
            "REFERENCIA:",
            "Ref:",
            "ref:",
            "REF:"
        };
        
        for (String pattern : patterns) {
            int index = notas.toLowerCase().indexOf(pattern.toLowerCase());
            if (index >= 0) {
                String referencia = notas.substring(index + pattern.length()).trim();
                // Tomar hasta el primer salto de línea o punto y coma
                int endIndex = Math.min(
                    referencia.indexOf('\n') >= 0 ? referencia.indexOf('\n') : referencia.length(),
                    referencia.indexOf(';') >= 0 ? referencia.indexOf(';') : referencia.length()
                );
                if (endIndex > 0) {
                    referencia = referencia.substring(0, endIndex).trim();
                }
                return referencia.isEmpty() ? null : referencia;
            }
        }
        
        // Si no encuentra patrón, verificar si las notas son muy cortas y podrían ser solo una referencia
        if (notas.length() < 100 && !notas.toLowerCase().contains("pedido") && 
            !notas.toLowerCase().contains("restaurante")) {
            return notas.trim();
        }
        
        return null;
    }
    
    private String formatDate(String fechaPedido) {
        if (fechaPedido == null || fechaPedido.isEmpty()) {
            return "Fecha no disponible";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(fechaPedido);

            if (date != null) {
                SimpleDateFormat outputFormat = new SimpleDateFormat("d 'de' MMMM, yyyy 'a las' HH:mm", 
                    new Locale("es", "ES"));
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            try {
                SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date2 = inputFormat2.parse(fechaPedido);
                if (date2 != null) {
                    SimpleDateFormat outputFormat = new SimpleDateFormat("d 'de' MMMM, yyyy", 
                        new Locale("es", "ES"));
                    return outputFormat.format(date2);
                }
            } catch (ParseException e2) {
                return fechaPedido;
            }
        }

        return fechaPedido;
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        rvOrders.setVisibility(loading ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(View.GONE);
    }

    private void showOrders() {
        rvOrders.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showEmptyState(String message) {
        rvOrders.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
        tvEmptyMessage.setText(message);
        progressBar.setVisibility(View.GONE);
    }
}

