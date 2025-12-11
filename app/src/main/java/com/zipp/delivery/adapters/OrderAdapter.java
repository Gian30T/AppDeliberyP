package com.zipp.delivery.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.zipp.delivery.R;
import com.zipp.delivery.network.response.PedidosResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<PedidosResponse.PedidoApi> orders;
    private OnOrderClickListener listener;
    private OnCancelOrderListener cancelListener;

    public interface OnOrderClickListener {
        void onOrderClick(PedidosResponse.PedidoApi pedido);
    }
    
    public interface OnCancelOrderListener {
        void onCancelOrder(PedidosResponse.PedidoApi pedido, int position);
    }
    
    public void setCancelOrderListener(OnCancelOrderListener listener) {
        this.cancelListener = listener;
    }

    public OrderAdapter(List<PedidosResponse.PedidoApi> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PedidosResponse.PedidoApi pedido = orders.get(position);
        holder.bind(pedido);
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView tvOrderId, tvOrderStatus, tvOrderDate;
        TextView tvPaymentMethod, tvSubtotal, tvDeliveryFee, tvOrderTotal;
        android.widget.LinearLayout itemsContainer;
        com.google.android.material.button.MaterialButton btnCancelOrder;

        ViewHolder(View itemView) {
            super(itemView);
            card = (MaterialCardView) itemView;
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);
            tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
            tvDeliveryFee = itemView.findViewById(R.id.tv_delivery_fee);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            itemsContainer = itemView.findViewById(R.id.items_container);
            btnCancelOrder = itemView.findViewById(R.id.btn_cancel_order);
        }

        void bind(PedidosResponse.PedidoApi pedido) {
            // Order ID
            tvOrderId.setText("Pedido #" + pedido.getId());

            // Status
            String estado = pedido.getEstadoPedido();
            tvOrderStatus.setText(pedido.getEstadoLegible());
            setStatusBackground(estado);

            // Date
            String fechaFormateada = formatDate(pedido.getFechaPedido());
            tvOrderDate.setText(fechaFormateada);

            // Payment Method
            String metodoPago = pedido.getMetodoPago();
            String metodoText = "Pago: " + (metodoPago != null && metodoPago.equals("efectivo") ? "Efectivo" : "Tarjeta");
            tvPaymentMethod.setText(metodoText);

            // Items List
            itemsContainer.removeAllViews();
            if (pedido.getItems() != null && !pedido.getItems().isEmpty()) {
                for (PedidosResponse.ItemPedidoApi item : pedido.getItems()) {
                    TextView itemView = createItemView(item);
                    itemsContainer.addView(itemView);
                }
            }

            // Subtotal (total - delivery fee)
            double subtotal = pedido.getMontoTotal() - pedido.getCostoEnvio();
            tvSubtotal.setText(String.format("S/ %.2f", subtotal));

            // Delivery Fee
            if (pedido.getCostoEnvio() > 0) {
                tvDeliveryFee.setText(String.format("S/ %.2f", pedido.getCostoEnvio()));
            } else {
                tvDeliveryFee.setText("Gratis");
            }

            // Total
            tvOrderTotal.setText(pedido.getFormattedTotal());

            // Cancel Button - solo mostrar si el pedido puede ser cancelado
            boolean canCancel = estado != null && 
                               (estado.equals("pendiente") || estado.equals("procesando"));
            btnCancelOrder.setVisibility(canCancel ? View.VISIBLE : View.GONE);
            
            btnCancelOrder.setOnClickListener(v -> {
                if (cancelListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        cancelListener.onCancelOrder(pedido, position);
                    }
                }
            });

            // Click listener para el card completo
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(pedido);
                }
            });
        }
        
        private TextView createItemView(PedidosResponse.ItemPedidoApi item) {
            TextView textView = new TextView(itemView.getContext());
            textView.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding(0, 4, 0, 4);
            textView.setTextSize(13);
            textView.setTextColor(itemView.getContext().getResources().getColor(R.color.text_secondary, null));
            
            String itemText = String.format("%d x %s - S/ %.2f", 
                item.getCantidad(),
                item.getProductoNombre() != null ? item.getProductoNombre() : "Producto",
                item.getSubtotal());
            textView.setText(itemText);
            
            return textView;
        }

        private void setStatusBackground(String estado) {
            int bgResId;
            int textColorResId = R.color.white;

            switch (estado) {
                case "pendiente":
                    bgResId = R.drawable.bg_status_pending;
                    break;
                case "procesando":
                    bgResId = R.drawable.bg_status_processing;
                    break;
                case "listo_para_recoger":
                case "en_camino":
                    bgResId = R.drawable.bg_status_in_transit;
                    break;
                case "entregado":
                    bgResId = R.drawable.bg_status_delivered;
                    break;
                case "cancelado":
                    bgResId = R.drawable.bg_status_cancelled;
                    break;
                default:
                    bgResId = R.drawable.bg_status_pending;
            }

            tvOrderStatus.setBackgroundResource(bgResId);
            tvOrderStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), textColorResId));
        }

        private String formatDate(String fechaPedido) {
            if (fechaPedido == null || fechaPedido.isEmpty()) {
                return "Fecha no disponible";
            }

            try {
                // Formato esperado de la API: "2024-12-10T13:30:00.000Z" o similar
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                Date date = inputFormat.parse(fechaPedido);

                if (date != null) {
                    SimpleDateFormat outputFormat = new SimpleDateFormat("d 'de' MMMM, yyyy", new Locale("es", "ES"));
                    return outputFormat.format(date);
                }
            } catch (ParseException e) {
                // Si falla el parsing, intentar otros formatos
                try {
                    SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date2 = inputFormat2.parse(fechaPedido);
                    if (date2 != null) {
                        SimpleDateFormat outputFormat = new SimpleDateFormat("d 'de' MMMM, yyyy", new Locale("es", "ES"));
                        return outputFormat.format(date2);
                    }
                } catch (ParseException e2) {
                    // Si todo falla, devolver la fecha original
                    return fechaPedido;
                }
            }

            return fechaPedido;
        }
    }
}

