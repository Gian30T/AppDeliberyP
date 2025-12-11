package com.zipp.delivery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zipp.delivery.R;
import com.zipp.delivery.adapters.RestaurantAdapter;
import com.zipp.delivery.models.Restaurant;
import com.zipp.delivery.utils.DataProvider;
import com.zipp.delivery.utils.FavoritesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FavoritesActivity extends AppCompatActivity {

    private static final String TAG = "FavoritesActivity";
    
    private ImageView ivBack;
    private TextView tvTitle;
    private RecyclerView rvFavorites;
    private ProgressBar progressBar;
    private View emptyState;
    private TextView tvEmptyMessage;

    private FavoritesManager favoritesManager;
    private RestaurantAdapter restaurantAdapter;
    private List<Restaurant> favoritesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesManager = FavoritesManager.getInstance(this);
        favoritesList = new ArrayList<>();

        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadFavorites();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        rvFavorites = findViewById(R.id.rv_favorites);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);
        tvEmptyMessage = findViewById(R.id.tv_empty_message);
    }

    private void setupRecyclerView() {
        restaurantAdapter = new RestaurantAdapter(favoritesList, this::onRestaurantClick, true);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        rvFavorites.setAdapter(restaurantAdapter);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
    }

    private void loadFavorites() {
        setLoading(true);
        
        // Obtener nombres de restaurantes favoritos desde el almacenamiento local
        Set<String> favoriteNames = favoritesManager.getFavoriteNames();
        
        if (favoriteNames != null && !favoriteNames.isEmpty()) {
            favoritesList.clear();
            
            // Mapear favoritos a objetos Restaurant desde DataProvider
            List<Restaurant> allRestaurants = new ArrayList<>();
            allRestaurants.addAll(DataProvider.getPopularRestaurants());
            allRestaurants.addAll(DataProvider.getNearbyRestaurants());
            
            for (String nombreRestaurante : favoriteNames) {
                // Buscar restaurante por nombre
                for (Restaurant restaurant : allRestaurants) {
                    if (restaurant.getName().equals(nombreRestaurante)) {
                        restaurant.setFavorite(true);
                        favoritesList.add(restaurant);
                        break;
                    }
                }
            }
            
            if (!favoritesList.isEmpty()) {
                restaurantAdapter.notifyDataSetChanged();
                showFavorites();
                Log.d(TAG, "Favoritos cargados: " + favoritesList.size());
            } else {
                showEmptyState("No tienes restaurantes favoritos");
            }
        } else {
            showEmptyState("No tienes restaurantes favoritos");
        }
        
        setLoading(false);
    }

    private void onRestaurantClick(Restaurant restaurant) {
        // Abrir detalle del restaurante
        Intent intent = new Intent(this, RestaurantActivity.class);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        rvFavorites.setVisibility(loading ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(View.GONE);
    }

    private void showFavorites() {
        progressBar.setVisibility(View.GONE);
        rvFavorites.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
    }

    private void showEmptyState(String message) {
        progressBar.setVisibility(View.GONE);
        rvFavorites.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
        tvEmptyMessage.setText(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar favoritos al volver (por si se eliminó alguno)
        loadFavorites();
    }
}

