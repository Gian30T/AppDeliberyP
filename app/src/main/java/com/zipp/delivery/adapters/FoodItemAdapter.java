package com.zipp.delivery.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.zipp.delivery.R;
import com.zipp.delivery.models.FoodItem;
import com.zipp.delivery.utils.CartManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FoodItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Object> items;
    private OnAddToCartListener listener;
    private CartManager cartManager;

    public interface OnAddToCartListener {
        void onAddToCart(FoodItem foodItem);
    }

    public FoodItemAdapter(List<FoodItem> foodItems, Map<String, List<FoodItem>> groupedItems, OnAddToCartListener listener) {
        this.listener = listener;
        this.cartManager = CartManager.getInstance();
        this.items = new ArrayList<>();

        // Flatten grouped items into list with headers
        for (Map.Entry<String, List<FoodItem>> entry : groupedItems.entrySet()) {
            items.add(entry.getKey()); // Header
            items.addAll(entry.getValue()); // Items
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_menu_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_food, parent, false);
            return new FoodViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((String) items.get(position));
        } else {
            ((FoodViewHolder) holder).bind((FoodItem) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        HeaderViewHolder(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tv_header);
        }

        void bind(String header) {
            tvHeader.setText(header);
        }
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        ImageView ivImage;
        TextView tvName, tvDescription, tvPrice, tvQuantity;
        MaterialButton btnAdd;
        View quantityControls;
        ImageView btnMinus, btnPlus;

        FoodViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_food);
            ivImage = itemView.findViewById(R.id.iv_image);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnAdd = itemView.findViewById(R.id.btn_add);
            quantityControls = itemView.findViewById(R.id.quantity_controls);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
        }

        void bind(FoodItem foodItem) {
            Log.d("FoodItemAdapter", "=== bind() llamado para: " + foodItem.getName() + " ===");
            
            tvName.setText(foodItem.getName());
            tvDescription.setText(foodItem.getDescription());
            tvPrice.setText(foodItem.getFormattedPrice());

            // Cargar imagen con Glide
            String imageUrl = foodItem.getImageUrl();
            int placeholderColorRes = getPlaceholderColor(foodItem);
            
            Log.d("FoodItemAdapter", "ImageUrl obtenida: " + (imageUrl != null ? imageUrl : "NULL"));
            Log.d("FoodItemAdapter", "ImageView: " + (ivImage != null ? "NO NULL" : "NULL"));
            
            // Limpiar la imagen primero
            if (ivImage != null) {
                ivImage.setImageDrawable(null);
                ivImage.setBackgroundResource(placeholderColorRes);
            }
            
            if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("null") && !imageUrl.trim().isEmpty()) {
                // Limpiar espacios en blanco y verificar que sea una URL válida
                imageUrl = imageUrl.trim();
                
                // Verificar que la URL comience con http:// o https://
                if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                    // Cargar imagen desde URL
                    int color = ivImage.getContext().getResources().getColor(placeholderColorRes, null);
                    ColorDrawable placeholder = new ColorDrawable(color);
                    ColorDrawable errorDrawable = new ColorDrawable(color);
                    
                    Log.d("FoodItemAdapter", "Cargando imagen para " + foodItem.getName() + ": " + imageUrl);
                    
                    Glide.with(ivImage.getContext())
                        .load(imageUrl)
                        .placeholder(placeholder)
                        .error(errorDrawable)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                        .centerCrop()
                        .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                            @Override
                            public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                                Log.e("FoodItemAdapter", "ERROR cargando imagen para " + foodItem.getName() + ": " + (e != null ? e.getMessage() : "Unknown error"));
                                if (e != null && e.getRootCauses() != null) {
                                    for (Throwable cause : e.getRootCauses()) {
                                        Log.e("FoodItemAdapter", "  Causa: " + cause.getMessage());
                                    }
                                }
                                return false; // Dejar que Glide maneje el error drawable
                            }

                            @Override
                            public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("FoodItemAdapter", "Imagen cargada EXITOSAMENTE para " + foodItem.getName());
                                return false;
                            }
                        })
                        .into(ivImage);
                } else {
                    Log.w("FoodItemAdapter", "URL inválida para " + foodItem.getName() + ": " + imageUrl);
                    // Si la URL no es válida, usar color placeholder
                    ivImage.setImageDrawable(null);
                    ivImage.setBackgroundResource(placeholderColorRes);
                }
            } else {
                Log.d("FoodItemAdapter", "Sin URL de imagen para " + foodItem.getName());
                // Si no hay URL, usar color placeholder
                ivImage.setImageDrawable(null);
                ivImage.setBackgroundResource(placeholderColorRes);
            }

            // Update quantity display
            int quantity = cartManager.getItemQuantity(foodItem.getId());
            updateQuantityDisplay(quantity);

            btnAdd.setOnClickListener(v -> {
                listener.onAddToCart(foodItem);
            });

            btnPlus.setOnClickListener(v -> {
                cartManager.incrementQuantity(foodItem);
                notifyItemChanged(getAdapterPosition());
            });

            btnMinus.setOnClickListener(v -> {
                cartManager.decrementQuantity(foodItem);
                notifyItemChanged(getAdapterPosition());
            });
        }

        private void updateQuantityDisplay(int quantity) {
            if (quantity > 0) {
                btnAdd.setVisibility(View.GONE);
                quantityControls.setVisibility(View.VISIBLE);
                tvQuantity.setText(String.valueOf(quantity));
            } else {
                btnAdd.setVisibility(View.VISIBLE);
                quantityControls.setVisibility(View.GONE);
            }
        }
        
        private int getPlaceholderColor(FoodItem foodItem) {
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
            } else {
                return R.color.primary_container;
            }
        }
    }
}



