package com.zipp.delivery.adapters;

import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.zipp.delivery.R;
import com.zipp.delivery.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartItems;
    private OnCartActionListener incrementListener;
    private OnCartActionListener decrementListener;
    private OnCartActionListener removeListener;

    public interface OnCartActionListener {
        void onAction(CartItem item);
    }

    public CartAdapter(List<CartItem> cartItems, 
                       OnCartActionListener incrementListener,
                       OnCartActionListener decrementListener,
                       OnCartActionListener removeListener) {
        this.cartItems = cartItems;
        this.incrementListener = incrementListener;
        this.decrementListener = decrementListener;
        this.removeListener = removeListener;
    }

    public void updateItems(List<CartItem> items) {
        this.cartItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        ImageView ivImage;
        TextView tvName, tvPrice, tvQuantity, tvTotal;
        ImageView btnMinus, btnPlus, btnRemove;

        ViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_cart_item);
            ivImage = itemView.findViewById(R.id.iv_image);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvTotal = itemView.findViewById(R.id.tv_total);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }

        void bind(CartItem item) {
            tvName.setText(item.getFoodItem().getName());
            tvPrice.setText(item.getFoodItem().getFormattedPrice());
            tvQuantity.setText(String.valueOf(item.getQuantity()));
            tvTotal.setText(item.getFormattedTotalPrice());

            // Cargar imagen con Glide
            String imageUrl = item.getFoodItem().getImageUrl();
            int placeholderColorRes = getPlaceholderColor(item.getFoodItem());
            
            // Limpiar la imagen primero
            ivImage.setImageDrawable(null);
            ivImage.setBackgroundResource(placeholderColorRes);
            
            if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("null") && !imageUrl.trim().isEmpty()) {
                // Limpiar espacios en blanco y verificar que sea una URL válida
                imageUrl = imageUrl.trim();
                
                // Verificar que la URL comience con http:// o https://
                if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                    // Cargar imagen desde URL
                    int color = ivImage.getContext().getResources().getColor(placeholderColorRes, null);
                    ColorDrawable placeholder = new ColorDrawable(color);
                    ColorDrawable errorDrawable = new ColorDrawable(color);
                    
                    Glide.with(ivImage.getContext())
                        .load(imageUrl)
                        .placeholder(placeholder)
                        .error(errorDrawable)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(ivImage);
                } else {
                    Log.w("CartAdapter", "URL inválida para " + item.getFoodItem().getName() + ": " + imageUrl);
                    // Si la URL no es válida, usar color placeholder
                    ivImage.setImageDrawable(null);
                    ivImage.setBackgroundResource(placeholderColorRes);
                }
            } else {
                Log.d("CartAdapter", "Sin URL de imagen para " + item.getFoodItem().getName());
                // Si no hay URL, usar color placeholder
                ivImage.setImageDrawable(null);
                ivImage.setBackgroundResource(placeholderColorRes);
            }

            btnPlus.setOnClickListener(v -> incrementListener.onAction(item));
            btnMinus.setOnClickListener(v -> decrementListener.onAction(item));
            btnRemove.setOnClickListener(v -> removeListener.onAction(item));
        }
        
        private int getPlaceholderColor(com.zipp.delivery.models.FoodItem foodItem) {
            // Colores placeholder según categoría
            String category = foodItem.getCategory().toLowerCase();
            if (category.contains("pizza")) {
                return R.color.cat_pizza;
            } else if (category.contains("burger") || category.contains("hamburguesa")) {
                return R.color.cat_burger;
            } else if (category.contains("sushi")) {
                return R.color.cat_sushi;
            } else if (category.contains("mexicana") || category.contains("taco")) {
                return R.color.cat_mexican;
            } else if (category.contains("china")) {
                return R.color.cat_chinese;
            } else if (category.contains("postre") || category.contains("dessert")) {
                return R.color.cat_dessert;
            } else if (category.contains("bebida") || category.contains("drink")) {
                return R.color.cat_drinks;
            } else if (category.contains("saludable") || category.contains("healthy")) {
                return R.color.cat_healthy;
            } else {
                // Usar color por defecto basado en ID
                int[] colors = {
                    R.color.cat_pizza, R.color.cat_burger, R.color.cat_sushi,
                    R.color.cat_mexican, R.color.cat_chinese, R.color.cat_dessert
                };
                return colors[foodItem.getId() % colors.length];
            }
        }
    }
}



