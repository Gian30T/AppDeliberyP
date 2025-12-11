package com.zipp.delivery.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.zipp.delivery.R;
import com.zipp.delivery.adapters.FoodItemAdapter;
import com.zipp.delivery.models.FoodItem;
import com.zipp.delivery.models.Restaurant;
import com.zipp.delivery.utils.CartManager;
import com.zipp.delivery.utils.DataProvider;
import com.zipp.delivery.utils.FavoritesManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestaurantActivity extends AppCompatActivity implements CartManager.CartUpdateListener {

    private MaterialCardView cardBack, cardFavorite;
    private ImageView ivBack, ivFavorite, ivRestaurantImage;
    private TextView tvName, tvCategory, tvRating, tvReviews, tvDeliveryTime, tvDeliveryFee, tvMinOrder;
    private RecyclerView rvMenu;
    private View cartBar;
    private TextView tvCartItems, tvCartTotal;
    private Button btnViewCart;

    private Restaurant restaurant;
    private CartManager cartManager;
    private FoodItemAdapter menuAdapter;
    private FavoritesManager favoritesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        favoritesManager = FavoritesManager.getInstance(this);
        cartManager = CartManager.getInstance();
        cartManager.addListener(this);

        loadRestaurant();
        initViews();
        setupRestaurantInfo();
        setupMenu();
        setupClickListeners();
        updateCartBar();
        
        // Verificar estado de favorito local
        checkFavoriteStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar estado de favorito al volver a la actividad
        checkFavoriteStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cartManager.removeListener(this);
    }

    private void loadRestaurant() {
        // Siempre cargar desde DataProvider para asegurar que tenga el menú completo con URLs
        int restaurantId = -1;
        
        // Intentar obtener el restaurante completo del Intent
        restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
        if (restaurant != null) {
            restaurantId = restaurant.getId();
        } else {
            restaurantId = getIntent().getIntExtra("restaurant_id", -1);
        }
        
        // SIEMPRE recargar desde DataProvider para asegurar que el menú tenga las URLs correctas
        if (restaurantId > 0) {
            Restaurant freshRestaurant = DataProvider.getRestaurantById(restaurantId);
            if (freshRestaurant != null) {
                // Preservar el estado de favorito si existía
                if (restaurant != null) {
                    freshRestaurant.setFavorite(restaurant.isFavorite());
                }
                restaurant = freshRestaurant;
            }
        }

        if (restaurant == null) {
            Toast.makeText(this, "Error al cargar restaurante", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        cardBack = findViewById(R.id.card_back);
        cardFavorite = findViewById(R.id.card_favorite);
        ivBack = findViewById(R.id.iv_back);
        ivFavorite = findViewById(R.id.iv_favorite);
        ivRestaurantImage = findViewById(R.id.iv_restaurant_image);
        tvName = findViewById(R.id.tv_name);
        tvCategory = findViewById(R.id.tv_category);
        tvRating = findViewById(R.id.tv_rating);
        tvReviews = findViewById(R.id.tv_reviews);
        tvDeliveryTime = findViewById(R.id.tv_delivery_time);
        tvDeliveryFee = findViewById(R.id.tv_delivery_fee);
        tvMinOrder = findViewById(R.id.tv_min_order);
        rvMenu = findViewById(R.id.rv_menu);
        cartBar = findViewById(R.id.cart_bar);
        tvCartItems = findViewById(R.id.tv_cart_items);
        tvCartTotal = findViewById(R.id.tv_cart_total);
        btnViewCart = findViewById(R.id.btn_view_cart);
    }

    private void setupRestaurantInfo() {
        tvName.setText(restaurant.getName());
        tvCategory.setText(restaurant.getCategory());
        tvRating.setText(String.valueOf(restaurant.getRating()));
        tvReviews.setText("(" + restaurant.getReviewCount() + " reseñas)");
        tvDeliveryTime.setText(restaurant.getDeliveryTime() + " min");
        tvDeliveryFee.setText(restaurant.getFormattedDeliveryFee());
        tvMinOrder.setText("Min: " + restaurant.getFormattedMinOrder());

        ivFavorite.setImageResource(restaurant.isFavorite() ? 
            R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline);
        
        // Cargar imagen del restaurante
        loadRestaurantImage();
    }
    
    private void loadRestaurantImage() {
        String imageUrl = restaurant.getImageUrl();
        int placeholderColorRes = getPlaceholderColorForCategory(restaurant.getCategory());
        
        // Limpiar la imagen primero
        ivRestaurantImage.setImageDrawable(null);
        ivRestaurantImage.setBackgroundResource(placeholderColorRes);
        
        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("null") && !imageUrl.trim().isEmpty()) {
            imageUrl = imageUrl.trim();
            
            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                int color = ivRestaurantImage.getContext().getResources().getColor(placeholderColorRes, null);
                ColorDrawable placeholder = new ColorDrawable(color);
                ColorDrawable errorDrawable = new ColorDrawable(color);
                
                Log.d("RestaurantActivity", "Cargando imagen para " + restaurant.getName() + ": " + imageUrl);
                
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(placeholder)
                    .error(errorDrawable)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(ivRestaurantImage);
            } else {
                Log.w("RestaurantActivity", "URL inválida para " + restaurant.getName() + ": " + imageUrl);
                ivRestaurantImage.setImageDrawable(null);
                ivRestaurantImage.setBackgroundResource(placeholderColorRes);
            }
        } else {
            Log.d("RestaurantActivity", "Sin URL de imagen para " + restaurant.getName());
            ivRestaurantImage.setImageDrawable(null);
            ivRestaurantImage.setBackgroundResource(placeholderColorRes);
        }
    }
    
    private int getPlaceholderColorForCategory(String category) {
        String cat = category.toLowerCase();
        if (cat.contains("pizza")) {
            return R.color.cat_pizza;
        } else if (cat.contains("burger") || cat.contains("hamburguesa")) {
            return R.color.cat_burger;
        } else if (cat.contains("sushi")) {
            return R.color.cat_sushi;
        } else if (cat.contains("mexicana") || cat.contains("taco")) {
            return R.color.cat_mexican;
        } else if (cat.contains("china") || cat.contains("chinese")) {
            return R.color.cat_chinese;
        } else if (cat.contains("postre") || cat.contains("dessert") || cat.contains("sweet")) {
            return R.color.cat_dessert;
        } else if (cat.contains("bebida") || cat.contains("drink") || cat.contains("juice")) {
            return R.color.cat_drinks;
        } else if (cat.contains("saludable") || cat.contains("healthy") || cat.contains("green")) {
            return R.color.cat_healthy;
        } else {
            return R.color.cat_pizza;
        }
    }

    private void setupMenu() {
        List<FoodItem> menu = restaurant.getMenu();
        if (menu == null) {
            menu = new ArrayList<>();
        }

        // Debug: Verificar URLs de imágenes
        Log.d("RestaurantActivity", "Total items en menú: " + menu.size());
        for (FoodItem item : menu) {
            String imageUrl = item.getImageUrl();
            Log.d("RestaurantActivity", "Item: " + item.getName() + 
                " | URL: " + (imageUrl != null ? imageUrl : "NULL") + 
                " | Vacío: " + (imageUrl == null || imageUrl.isEmpty()));
        }

        // Group items by category
        Map<String, List<FoodItem>> groupedMenu = new LinkedHashMap<>();
        for (FoodItem item : menu) {
            String category = item.getCategory();
            if (!groupedMenu.containsKey(category)) {
                groupedMenu.put(category, new ArrayList<>());
            }
            groupedMenu.get(category).add(item);
        }

        menuAdapter = new FoodItemAdapter(menu, groupedMenu, this::onAddToCart);
        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        rvMenu.setAdapter(menuAdapter);
    }

    private void setupClickListeners() {
        // Usar el CardView si existe, sino usar el ImageView directamente
        if (cardBack != null) {
            cardBack.setOnClickListener(v -> onBackPressed());
        } else {
            ivBack.setOnClickListener(v -> onBackPressed());
        }

        // Usar el CardView si existe, sino usar el ImageView directamente
        View.OnClickListener favoriteClickListener = v -> toggleFavorite();
        
        if (cardFavorite != null) {
            cardFavorite.setOnClickListener(favoriteClickListener);
        } else {
            ivFavorite.setOnClickListener(favoriteClickListener);
        }

        btnViewCart.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });

        cartBar.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });
    }

    private void onAddToCart(FoodItem foodItem) {
        addItemToCart(foodItem);
    }

    private void addItemToCart(FoodItem foodItem) {
        // Agregar item con el delivery fee del restaurante
        cartManager.addItem(foodItem, restaurant.getName(), restaurant.getDeliveryFee());
        Toast.makeText(this, foodItem.getName() + " agregado", Toast.LENGTH_SHORT).show();
    }

    private void updateCartBar() {
        int itemCount = cartManager.getTotalItemCount();
        if (itemCount > 0) {
            cartBar.setVisibility(View.VISIBLE);
            tvCartItems.setText(itemCount + " artículo" + (itemCount > 1 ? "s" : ""));
            tvCartTotal.setText(cartManager.getFormattedTotal());
        } else {
            cartBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCartUpdated(int itemCount, double total) {
        runOnUiThread(() -> {
            updateCartBar();
            if (menuAdapter != null) {
                menuAdapter.notifyDataSetChanged();
            }
        });
    }

    private void checkFavoriteStatus() {
        if (restaurant == null) return;
        
        String nombreRestaurante = restaurant.getName();
        boolean esFavorito = favoritesManager.isFavorite(nombreRestaurante);
        restaurant.setFavorite(esFavorito);
        updateFavoriteIcon();
    }

    private void toggleFavorite() {
        if (restaurant == null) return;
        
        String nombreRestaurante = restaurant.getName();
        
        if (restaurant.isFavorite()) {
            // Eliminar de favoritos
            favoritesManager.removeFavorite(nombreRestaurante);
            restaurant.setFavorite(false);
            Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
        } else {
            // Agregar a favoritos
            favoritesManager.addFavorite(nombreRestaurante);
            restaurant.setFavorite(true);
            Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show();
        }
        
        updateFavoriteIcon();
    }

    private void updateFavoriteIcon() {
        ivFavorite.setImageResource(restaurant.isFavorite() ? 
            R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline);
    }
}



