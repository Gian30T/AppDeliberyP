package com.zipp.delivery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.zipp.delivery.R;
import com.zipp.delivery.adapters.CategoryAdapter;
import com.zipp.delivery.adapters.RestaurantAdapter;
import com.zipp.delivery.models.Category;
import com.zipp.delivery.models.Restaurant;
import com.zipp.delivery.network.ApiClient;
import com.zipp.delivery.network.response.CategoriasResponse;
import com.zipp.delivery.network.response.ProductosResponse;
import com.zipp.delivery.utils.CartManager;
import com.zipp.delivery.utils.DataProvider;
import com.zipp.delivery.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements CartManager.CartUpdateListener {

    private static final String TAG = "MainActivity";
    
    private TextView tvGreeting, tvCartBadge;
    private ImageView ivProfile;
    private MaterialCardView searchCard;
    private RecyclerView rvCategories, rvPopularRestaurants, rvNearbyRestaurants;
    private BottomNavigationView bottomNav;
    private View cartBadgeContainer;

    private SessionManager sessionManager;
    private CartManager cartManager;
    private ApiClient apiClient;

    private CategoryAdapter categoryAdapter;
    private RestaurantAdapter popularAdapter;
    private RestaurantAdapter nearbyAdapter;
    
    // Lista de categorías de la API
    private List<Category> apiCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        apiClient = ApiClient.getInstance(this);
        
        // Verificar si el usuario está logueado
        if (!sessionManager.isLoggedIn()) {
            goToLogin();
            return;
        }
        
        cartManager = CartManager.getInstance();
        cartManager.addListener(this);

        initViews();
        setupGreeting();
        setupRecyclerViews();
        setupBottomNavigation();
        setupClickListeners();
        
        // Cargar datos desde la API
        loadCategoriesFromApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
        // Asegurar que el item de inicio esté seleccionado
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartManager != null) {
            cartManager.removeListener(this);
        }
    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tv_greeting);
        ivProfile = findViewById(R.id.iv_profile);
        searchCard = findViewById(R.id.search_card);
        rvCategories = findViewById(R.id.rv_categories);
        rvPopularRestaurants = findViewById(R.id.rv_popular_restaurants);
        rvNearbyRestaurants = findViewById(R.id.rv_nearby_restaurants);
        bottomNav = findViewById(R.id.bottom_nav);
        tvCartBadge = findViewById(R.id.tv_cart_badge);
        cartBadgeContainer = findViewById(R.id.cart_badge_container);
    }

    private void setupGreeting() {
        String userName = sessionManager.getUserName();
        String firstName = userName.split(" ")[0];
        tvGreeting.setText(String.format(getString(R.string.hello_user), firstName));
    }

    private void setupRecyclerViews() {
        // Categories - inicialmente vacío, se llenará con la API
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), category -> {
            // Filtrar productos por categoría
            loadProductsByCategory(category.getId());
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        // Popular Restaurants - usar datos locales por ahora
        List<Restaurant> popularRestaurants = DataProvider.getPopularRestaurants();
        popularAdapter = new RestaurantAdapter(popularRestaurants, this::openRestaurant, false);
        rvPopularRestaurants.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvPopularRestaurants.setAdapter(popularAdapter);

        // Nearby Restaurants - usar datos locales por ahora
        List<Restaurant> nearbyRestaurants = DataProvider.getNearbyRestaurants();
        nearbyAdapter = new RestaurantAdapter(nearbyRestaurants, this::openRestaurant, true);
        rvNearbyRestaurants.setLayoutManager(new LinearLayoutManager(this));
        rvNearbyRestaurants.setAdapter(nearbyAdapter);
    }
    
    /**
     * Cargar categorías desde la API
     */
    private void loadCategoriesFromApi() {
        apiClient.getCategoriasApi().obtenerTodas().enqueue(new Callback<CategoriasResponse>() {
            @Override
            public void onResponse(Call<CategoriasResponse> call, Response<CategoriasResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoriasResponse.CategoriaApi> categoriasApi = response.body().getCategorias();
                    
                    if (categoriasApi != null && !categoriasApi.isEmpty()) {
                        // Convertir categorías de API a modelo local
                        apiCategories.clear();
                        for (CategoriasResponse.CategoriaApi cat : categoriasApi) {
                            int iconRes = getIconForCategory(cat.getNombre());
                            int colorRes = getColorForCategory(cat.getNombre());
                            apiCategories.add(new Category(cat.getId(), cat.getNombre(), iconRes, colorRes));
                        }
                        
                        // Actualizar adapter
                        categoryAdapter = new CategoryAdapter(apiCategories, category -> {
                            loadProductsByCategory(category.getId());
                        });
                        rvCategories.setAdapter(categoryAdapter);
                        
                        Log.d(TAG, "Categorías cargadas: " + apiCategories.size());
                    }
                } else {
                    Log.e(TAG, "Error al cargar categorías: " + response.code());
                    // Usar categorías locales como fallback
                    loadLocalCategories();
                }
            }

            @Override
            public void onFailure(Call<CategoriasResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage());
                // Usar categorías locales como fallback
                loadLocalCategories();
            }
        });
    }
    
    /**
     * Cargar categorías locales como fallback
     */
    private void loadLocalCategories() {
        List<Category> categories = DataProvider.getCategories();
        categoryAdapter = new CategoryAdapter(categories, category -> {
            // Por ahora solo muestra un toast
            Toast.makeText(this, "Categoría: " + category.getName(), Toast.LENGTH_SHORT).show();
        });
        rvCategories.setAdapter(categoryAdapter);
    }
    
    /**
     * Cargar productos por categoría desde la API
     */
    private void loadProductsByCategory(int categoryId) {
        apiClient.getProductosApi().obtenerPorCategoria(categoryId).enqueue(new Callback<ProductosResponse>() {
            @Override
            public void onResponse(Call<ProductosResponse> call, Response<ProductosResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductosResponse.ProductoApi> productos = response.body().getProductos();
                    if (productos != null && !productos.isEmpty()) {
                        Toast.makeText(MainActivity.this, 
                            productos.size() + " productos encontrados", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, 
                            "No hay productos en esta categoría", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductosResponse> call, Throwable t) {
                Log.e(TAG, "Error al cargar productos: " + t.getMessage());
            }
        });
    }
    
    /**
     * Obtener icono según nombre de categoría
     */
    private int getIconForCategory(String nombre) {
        switch (nombre.toLowerCase()) {
            case "pizzas":
            case "pizza":
                return R.drawable.ic_pizza;
            case "hamburguesas":
            case "burger":
                return R.drawable.ic_burger;
            case "sushi":
                return R.drawable.ic_sushi;
            case "mexicana":
                return R.drawable.ic_mexican;
            case "china":
            case "chinese":
                return R.drawable.ic_chinese;
            case "postres":
            case "dessert":
                return R.drawable.ic_dessert;
            case "bebidas":
            case "drinks":
                return R.drawable.ic_drinks;
            case "saludable":
            case "healthy":
                return R.drawable.ic_healthy;
            default:
                return R.drawable.ic_pizza;
        }
    }
    
    /**
     * Obtener color según nombre de categoría
     */
    private int getColorForCategory(String nombre) {
        switch (nombre.toLowerCase()) {
            case "pizzas":
            case "pizza":
                return R.color.cat_pizza;
            case "hamburguesas":
            case "burger":
                return R.color.cat_burger;
            case "sushi":
                return R.color.cat_sushi;
            case "mexicana":
                return R.color.cat_mexican;
            case "china":
            case "chinese":
                return R.color.cat_chinese;
            case "postres":
            case "dessert":
                return R.color.cat_dessert;
            case "bebidas":
            case "drinks":
                return R.color.cat_drinks;
            case "saludable":
            case "healthy":
                return R.color.cat_healthy;
            default:
                return R.color.primary;
        }
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_home);
        
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void setupClickListeners() {
        ivProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        searchCard.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
        });

        findViewById(R.id.tv_see_all_popular).setOnClickListener(v -> {
            Toast.makeText(this, "Ver todos - Próximamente", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.tv_see_all_nearby).setOnClickListener(v -> {
            Toast.makeText(this, "Ver todos - Próximamente", Toast.LENGTH_SHORT).show();
        });

        cartBadgeContainer.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });
    }

    private void openRestaurant(Restaurant restaurant) {
        Intent intent = new Intent(this, RestaurantActivity.class);
        intent.putExtra("restaurant_id", restaurant.getId());
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }

    private void updateCartBadge() {
        int itemCount = cartManager.getTotalItemCount();
        if (itemCount > 0) {
            cartBadgeContainer.setVisibility(View.VISIBLE);
            tvCartBadge.setText(String.valueOf(itemCount));
        } else {
            cartBadgeContainer.setVisibility(View.GONE);
        }
    }
    
    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCartUpdated(int itemCount, double total) {
        runOnUiThread(this::updateCartBadge);
    }
}
