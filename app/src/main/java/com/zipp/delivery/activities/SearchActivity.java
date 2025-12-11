package com.zipp.delivery.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zipp.delivery.R;
import com.zipp.delivery.adapters.FoodItemAdapter;
import com.zipp.delivery.models.FoodItem;
import com.zipp.delivery.models.Restaurant;
import com.zipp.delivery.network.ApiClient;
import com.zipp.delivery.network.response.ProductosResponse;
import com.zipp.delivery.utils.CartManager;
import com.zipp.delivery.utils.DataProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements CartManager.CartUpdateListener {

    private static final String TAG = "SearchActivity";
    private static final int MIN_SEARCH_LENGTH = 2;
    private static final long SEARCH_DELAY_MS = 500; // Delay para evitar muchas búsquedas

    private ImageView ivBack, ivClear;
    private EditText etSearch;
    private RecyclerView rvResults;
    private ProgressBar progressBar;
    private LinearLayout emptyState, noResultsState;

    private ApiClient apiClient;
    private CartManager cartManager;
    private FoodItemAdapter adapter;
    private List<FoodItem> currentProducts = new ArrayList<>();

    private android.os.Handler searchHandler = new android.os.Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        apiClient = ApiClient.getInstance(this);
        cartManager = CartManager.getInstance();
        cartManager.addListener(this);

        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupSearchListener();
        
        // Focus en el campo de búsqueda al abrir
        etSearch.requestFocus();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivClear = findViewById(R.id.iv_clear);
        etSearch = findViewById(R.id.et_search);
        rvResults = findViewById(R.id.rv_results);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);
        noResultsState = findViewById(R.id.no_results_state);
    }

    private void setupRecyclerView() {
        adapter = new FoodItemAdapter(
            new ArrayList<>(),
            new LinkedHashMap<>(),
            this::onProductClick
        );
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(adapter);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        ivClear.setOnClickListener(v -> {
            etSearch.setText("");
            etSearch.requestFocus();
        });

        // Buscar cuando presiona Enter
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                performSearch(etSearch.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                
                // Mostrar/ocultar botón limpiar
                ivClear.setVisibility(query.length() > 0 ? View.VISIBLE : View.GONE);

                // Cancelar búsqueda anterior
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                // Si está vacío, mostrar estado inicial
                if (query.isEmpty()) {
                    showEmptyState();
                    return;
                }

                // Si es muy corto, no buscar
                if (query.length() < MIN_SEARCH_LENGTH) {
                    showEmptyState();
                    return;
                }

                // Buscar con delay
                searchRunnable = () -> performSearch(query);
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch(String query) {
        if (query.length() < MIN_SEARCH_LENGTH) {
            return;
        }

        Log.d(TAG, "Buscando: " + query);
        Log.d(TAG, "URL base de la API: http://192.168.100.17:3000/api/");
        setLoading(true);
        hideAllStates();

        Call<ProductosResponse> call = apiClient.getProductosApi().buscar(query);
        Log.d(TAG, "Request URL: " + call.request().url());
        Log.d(TAG, "Request method: " + call.request().method());
        
        call.enqueue(new Callback<ProductosResponse>() {
            @Override
            public void onResponse(Call<ProductosResponse> call, Response<ProductosResponse> response) {
                setLoading(false);

                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response successful: " + response.isSuccessful());
                Log.d(TAG, "Response body: " + (response.body() != null ? "not null" : "null"));

                // Log de respuesta completa
                Log.d(TAG, "Response raw: " + response.raw());
                if (response.body() != null) {
                    Log.d(TAG, "Response body tipo: " + response.body().getClass().getName());
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductosResponse.ProductoApi> productosApi = response.body().getProductos();
                    Log.d(TAG, "Productos recibidos: " + (productosApi != null ? productosApi.size() : 0));

                    if (productosApi != null && !productosApi.isEmpty()) {
                        // Convertir productos de API a modelo local
                        currentProducts = convertApiProductsToFoodItems(productosApi);
                        Log.d(TAG, "Productos convertidos: " + currentProducts.size());
                        
                        // Agrupar por categoría
                        Map<String, List<FoodItem>> groupedProducts = new LinkedHashMap<>();
                        for (FoodItem item : currentProducts) {
                            String category = item.getCategory();
                            if (!groupedProducts.containsKey(category)) {
                                groupedProducts.put(category, new ArrayList<>());
                            }
                            groupedProducts.get(category).add(item);
                        }

                        adapter = new FoodItemAdapter(currentProducts, groupedProducts, SearchActivity.this::onProductClick);
                        rvResults.setAdapter(adapter);

                        showResults();
                        Log.d(TAG, "Productos encontrados: " + currentProducts.size());
                    } else {
                        Log.d(TAG, "No hay productos o la lista está vacía");
                        showNoResults();
                    }
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error leyendo errorBody", e);
                    }
                    Log.e(TAG, "Error en búsqueda - Code: " + response.code() + ", Error: " + errorBody);
                    Toast.makeText(SearchActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    showNoResults();
                }
            }

            @Override
            public void onFailure(Call<ProductosResponse> call, Throwable t) {
                setLoading(false);
                String errorMessage = t.getMessage();
                
                // Log completo del error
                Log.e(TAG, "Error de conexión completo: ", t);
                Log.e(TAG, "Error message: " + errorMessage);
                Log.e(TAG, "Error class: " + t.getClass().getName());
                
                // Mostrar mensaje más amigable
                if (t instanceof java.net.UnknownHostException) {
                    Toast.makeText(SearchActivity.this, "No se pudo conectar al servidor. Verifica tu conexión.", Toast.LENGTH_LONG).show();
                } else if (t instanceof java.net.ConnectException) {
                    Toast.makeText(SearchActivity.this, "Error de conexión. Verifica que la API esté corriendo.", Toast.LENGTH_LONG).show();
                } else if (t instanceof java.lang.IllegalStateException) {
                    Log.e(TAG, "IllegalStateException - Probable error de parsing JSON", t);
                    Toast.makeText(SearchActivity.this, "Error al procesar respuesta del servidor. Revisa logs.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SearchActivity.this, "Error: " + (errorMessage != null ? errorMessage : "Desconocido"), Toast.LENGTH_LONG).show();
                }
                
                showNoResults();
            }
        });
    }

    /**
     * Convertir productos de API a modelo FoodItem local
     */
    private List<FoodItem> convertApiProductsToFoodItems(List<ProductosResponse.ProductoApi> productosApi) {
        List<FoodItem> productos = new ArrayList<>();
        
        for (ProductosResponse.ProductoApi productoApi : productosApi) {
            FoodItem item = new FoodItem(
                (int) productoApi.getId(),
                productoApi.getNombre(),
                productoApi.getDescripcion() != null ? productoApi.getDescripcion() : "",
                productoApi.getUrlImagen() != null ? productoApi.getUrlImagen() : "",
                productoApi.getPrecio(),
                productoApi.getCategoriaNombre() != null ? productoApi.getCategoriaNombre() : "General",
                productoApi.getCategoriaId()
            );
            item.setAvailable(productoApi.isDisponible());
            productos.add(item);
        }
        
        return productos;
    }

    private void onProductClick(FoodItem foodItem) {
        addItemToCart(foodItem);
    }

    private void addItemToCart(FoodItem foodItem) {
        // Obtener información del restaurante por ID
        String restaurantName = "Restaurante";
        double deliveryFee = 0.0;
        
        Restaurant restaurant = DataProvider.getRestaurantById(foodItem.getRestaurantId());
        if (restaurant != null) {
            restaurantName = restaurant.getName();
            deliveryFee = restaurant.getDeliveryFee();
        }

        cartManager.addItem(foodItem, restaurantName, deliveryFee);
        Toast.makeText(this, foodItem.getName() + " agregado al carrito", Toast.LENGTH_SHORT).show();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        rvResults.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        noResultsState.setVisibility(View.GONE);
        rvResults.setVisibility(View.GONE);
    }

    private void showNoResults() {
        emptyState.setVisibility(View.GONE);
        noResultsState.setVisibility(View.VISIBLE);
        rvResults.setVisibility(View.GONE);
    }

    private void showResults() {
        emptyState.setVisibility(View.GONE);
        noResultsState.setVisibility(View.GONE);
        rvResults.setVisibility(View.VISIBLE);
    }

    private void hideAllStates() {
        emptyState.setVisibility(View.GONE);
        noResultsState.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        if (cartManager != null) {
            cartManager.removeListener(this);
        }
    }

    @Override
    public void onCartUpdated(int itemCount, double total) {
        runOnUiThread(() -> {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });
    }
}

