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
import com.zipp.delivery.models.Restaurant;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<Restaurant> restaurants;
    private OnRestaurantClickListener listener;
    private boolean isVertical;

    public interface OnRestaurantClickListener {
        void onRestaurantClick(Restaurant restaurant);
    }

    public RestaurantAdapter(List<Restaurant> restaurants, OnRestaurantClickListener listener, boolean isVertical) {
        this.restaurants = restaurants;
        this.listener = listener;
        this.isVertical = isVertical;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = isVertical ? R.layout.item_restaurant_vertical : R.layout.item_restaurant_horizontal;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.bind(restaurant);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        ImageView ivImage;
        TextView tvName, tvCategory, tvRating, tvDeliveryTime, tvDeliveryFee;

        ViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_restaurant);
            ivImage = itemView.findViewById(R.id.iv_image);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvDeliveryTime = itemView.findViewById(R.id.tv_delivery_time);
            tvDeliveryFee = itemView.findViewById(R.id.tv_delivery_fee);
        }

        void bind(Restaurant restaurant) {
            tvName.setText(restaurant.getName());
            tvCategory.setText(restaurant.getCategory());
            tvRating.setText(String.valueOf(restaurant.getRating()));
            tvDeliveryTime.setText(restaurant.getDeliveryTime() + " min");
            
            if (tvDeliveryFee != null) {
                tvDeliveryFee.setText(restaurant.getFormattedDeliveryFee());
            }

            // Obtener color placeholder según categoría
            int placeholderColorRes = getPlaceholderColor(restaurant);
            
            // Cargar imagen con Glide
            String imageUrl = restaurant.getImageUrl();
            
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
                    
                    Log.d("RestaurantAdapter", "Cargando imagen para " + restaurant.getName() + ": " + imageUrl);
                    
                    Glide.with(ivImage.getContext())
                        .load(imageUrl)
                        .placeholder(placeholder)
                        .error(errorDrawable)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(ivImage);
                } else {
                    Log.w("RestaurantAdapter", "URL inválida para " + restaurant.getName() + ": " + imageUrl);
                    // Si la URL no es válida, usar color placeholder
                    ivImage.setImageDrawable(null);
                    ivImage.setBackgroundResource(placeholderColorRes);
                }
            } else {
                Log.d("RestaurantAdapter", "Sin URL de imagen para " + restaurant.getName());
                // Si no hay URL, usar color placeholder
                ivImage.setImageDrawable(null);
                ivImage.setBackgroundResource(placeholderColorRes);
            }

            itemView.setOnClickListener(v -> listener.onRestaurantClick(restaurant));
        }
        
        private int getPlaceholderColor(Restaurant restaurant) {
            // Colores placeholder según categoría
            String category = restaurant.getCategory().toLowerCase();
            if (category.contains("pizza")) {
                return R.color.cat_pizza;
            } else if (category.contains("burger") || category.contains("hamburguesa")) {
                return R.color.cat_burger;
            } else if (category.contains("sushi")) {
                return R.color.cat_sushi;
            } else if (category.contains("mexicana") || category.contains("taco")) {
                return R.color.cat_mexican;
            } else if (category.contains("china") || category.contains("chinese")) {
                return R.color.cat_chinese;
            } else if (category.contains("postre") || category.contains("dessert") || category.contains("sweet")) {
                return R.color.cat_dessert;
            } else if (category.contains("bebida") || category.contains("drink") || category.contains("juice")) {
                return R.color.cat_drinks;
            } else if (category.contains("saludable") || category.contains("healthy") || category.contains("green")) {
                return R.color.cat_healthy;
            } else if (category.contains("parrilla") || category.contains("grill")) {
                return R.color.cat_burger; // Usar color similar
            } else if (category.contains("italiana") || category.contains("pasta")) {
                return R.color.cat_pizza; // Usar color similar
            } else {
                // Usar color por defecto basado en ID
                int[] colors = {
                    R.color.cat_pizza, R.color.cat_burger, R.color.cat_sushi,
                    R.color.cat_mexican, R.color.cat_chinese, R.color.cat_dessert
                };
                return colors[restaurant.getId() % colors.length];
            }
        }
    }
}



