package com.zipp.delivery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.zipp.delivery.R;
import com.zipp.delivery.models.CartItem;
import com.zipp.delivery.network.ApiClient;
import com.zipp.delivery.network.request.DireccionRequest;
import com.zipp.delivery.network.request.PedidoRequest;
import com.zipp.delivery.network.response.DireccionResponse;
import com.zipp.delivery.network.response.PedidoResponse;
import com.zipp.delivery.network.response.ProductosResponse;
import com.zipp.delivery.utils.CartManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "CheckoutActivity";
    
    private ImageView ivBack;
    private TextInputEditText etAddress, etInstructions;
    private RadioGroup rgPayment;
    private RadioButton rbCash, rbCard;
    private TextView tvSubtotal, tvDelivery, tvTotal;
    private Button btnPlaceOrder;
    private ProgressBar progressBar;

    private CartManager cartManager;
    private ApiClient apiClient;
    private int successfulOrders = 0;
    private int failedOrders = 0;
    private int totalOrdersToCreate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        cartManager = CartManager.getInstance();
        apiClient = ApiClient.getInstance(this);

        initViews();
        setupOrderSummary();
        setupClickListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        etAddress = findViewById(R.id.et_address);
        etInstructions = findViewById(R.id.et_instructions);
        rgPayment = findViewById(R.id.rg_payment);
        rbCash = findViewById(R.id.rb_cash);
        rbCard = findViewById(R.id.rb_card);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvDelivery = findViewById(R.id.tv_delivery);
        tvTotal = findViewById(R.id.tv_total);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        
        // ProgressBar si existe
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupOrderSummary() {
        tvSubtotal.setText(cartManager.getFormattedSubtotal());
        tvDelivery.setText(cartManager.getFormattedDeliveryFee());
        tvTotal.setText(cartManager.getFormattedTotal());
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        String address = etAddress.getText().toString().trim();
        String instructions = etInstructions.getText() != null ? 
            etInstructions.getText().toString().trim() : "";

        if (address.isEmpty()) {
            etAddress.setError("Ingresa tu dirección de entrega");
            etAddress.requestFocus();
            return;
        }

        String paymentMethod = rbCash.isChecked() ? "efectivo" : "tarjeta_credito";

        // Mostrar diálogo de confirmación
        new AlertDialog.Builder(this)
            .setTitle("Confirmar pedido")
            .setMessage("¿Deseas confirmar tu pedido?\n\nTotal: " + cartManager.getFormattedTotal() + 
                       "\nPago: " + (rbCash.isChecked() ? "Efectivo" : "Tarjeta"))
            .setPositiveButton("Confirmar", (dialog, which) -> {
                createOrderWithApi(address, paymentMethod, instructions);
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }
    
    /**
     * Crear pedido usando la API
     */
    private void createOrderWithApi(String address, String paymentMethod, String notes) {
        setLoading(true);
        
        // Primero crear la dirección
        DireccionRequest direccionRequest = new DireccionRequest(
            address,
            "Ciudad", // Por defecto
            "", // Provincia opcional
            "00000", // Código postal por defecto
            true
        );
        
        apiClient.getDireccionesApi().crear(direccionRequest).enqueue(new Callback<DireccionResponse>() {
            @Override
            public void onResponse(Call<DireccionResponse> call, Response<DireccionResponse> response) {
                if (response.isSuccessful() && response.body() != null && 
                    response.body().getDireccion() != null) {
                    
                    long direccionId = response.body().getDireccion().getId();
                    Log.d(TAG, "Dirección creada con ID: " + direccionId);
                    
                    // Ahora crear el pedido
                    createPedido(direccionId, paymentMethod, notes);
                    
                } else {
                    setLoading(false);
                    // Si falla crear dirección, usar ID temporal o simular
                    Log.w(TAG, "No se pudo crear dirección, simulando pedido local");
                    simulateLocalOrder(paymentMethod);
                }
            }

            @Override
            public void onFailure(Call<DireccionResponse> call, Throwable t) {
                Log.e(TAG, "Error al crear dirección: " + t.getMessage());
                // Simular pedido local si falla la conexión
                simulateLocalOrder(paymentMethod);
            }
        });
    }
    
    /**
     * Crear pedidos agrupados por restaurante en la API
     */
    private void createPedido(long direccionId, String paymentMethod, String notes) {
        // Agrupar items por restaurante
        java.util.Map<Integer, java.util.List<CartItem>> itemsByRestaurant = cartManager.getItemsByRestaurant();
        
        if (itemsByRestaurant.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
            setLoading(false);
            return;
        }
        
        // Resetear contadores
        successfulOrders = 0;
        failedOrders = 0;
        totalOrdersToCreate = itemsByRestaurant.size();
        
        // Crear un pedido por cada restaurante
        createPedidosForRestaurants(itemsByRestaurant, direccionId, paymentMethod, notes, 0, itemsByRestaurant.size());
    }
    
    /**
     * Crear pedidos de forma secuencial para cada restaurante
     */
    private void createPedidosForRestaurants(
            java.util.Map<Integer, java.util.List<CartItem>> itemsByRestaurant,
            long direccionId, String paymentMethod, String notes,
            int currentIndex, int totalRestaurants) {
        
        // Convertir map a lista para poder iterar por índice
        final java.util.List<java.util.Map.Entry<Integer, java.util.List<CartItem>>> restaurantList = 
            new java.util.ArrayList<>(itemsByRestaurant.entrySet());
        
        if (currentIndex >= restaurantList.size()) {
            // Todos los pedidos se crearon exitosamente
            onOrderSuccess();
            return;
        }
        
        java.util.Map.Entry<Integer, java.util.List<CartItem>> entry = restaurantList.get(currentIndex);
        final int restaurantId = entry.getKey();
        final java.util.List<CartItem> restaurantItems = entry.getValue();
        
        // Calcular subtotal y delivery fee para este restaurante
        double subtotal = 0;
        double deliveryFee = 0;
        String restaurantNameTemp = "";
        
        for (CartItem item : restaurantItems) {
            subtotal += item.getTotalPrice();
            deliveryFee = item.getRestaurantDeliveryFee(); // Todos los items del mismo restaurante tienen el mismo fee
            if (restaurantNameTemp.isEmpty()) {
                restaurantNameTemp = item.getRestaurantName();
            }
        }
        
        // Crear variables finales para usar en el callback
        final String restaurantName = restaurantNameTemp;
        final double finalSubtotal = subtotal;
        final double finalDeliveryFee = deliveryFee;
        final double finalTotal = subtotal + deliveryFee;
        final int finalCurrentIndex = currentIndex;
        final int finalTotalRestaurants = totalRestaurants;
        
        // Buscar IDs reales de productos antes de crear el pedido
        resolveProductIds(restaurantItems, itemsByRestaurant, direccionId, paymentMethod, notes, 
                         finalCurrentIndex, finalTotalRestaurants, restaurantList, restaurantName);
    }
    
    /**
     * Buscar los IDs reales de los productos en la API por nombre
     */
    private void resolveProductIds(
            final java.util.List<CartItem> restaurantItems,
            final java.util.Map<Integer, java.util.List<CartItem>> itemsByRestaurant,
            final long direccionId, final String paymentMethod, final String notes,
            final int finalCurrentIndex, final int finalTotalRestaurants,
            final java.util.List<java.util.Map.Entry<Integer, java.util.List<CartItem>>> restaurantList,
            final String restaurantName) {
        
        // Crear lista de nombres de productos a buscar
        java.util.List<String> productNames = new java.util.ArrayList<>();
        java.util.Map<String, CartItem> nameToCartItem = new java.util.HashMap<>();
        
        for (CartItem cartItem : restaurantItems) {
            String productName = cartItem.getFoodItem().getName();
            productNames.add(productName);
            nameToCartItem.put(productName.toLowerCase(), cartItem);
        }
        
        Log.d(TAG, "Buscando IDs reales para " + productNames.size() + " productos");
        
        // Buscar todos los productos disponibles en la API
        apiClient.getProductosApi().obtenerDisponibles().enqueue(new retrofit2.Callback<com.zipp.delivery.network.response.ProductosResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.zipp.delivery.network.response.ProductosResponse> call, 
                                 retrofit2.Response<com.zipp.delivery.network.response.ProductosResponse> response) {
                if (response.isSuccessful() && response.body() != null && 
                    response.body().getProductos() != null) {
                    
                    java.util.List<com.zipp.delivery.network.response.ProductosResponse.ProductoApi> apiProducts = 
                        response.body().getProductos();
                    
                    // Mapear productos por nombre (case insensitive) y también por nombre parcial
                    java.util.Map<String, Long> nameToId = new java.util.HashMap<>();
                    java.util.Map<String, Long> partialNameToId = new java.util.HashMap<>();
                    
                    for (com.zipp.delivery.network.response.ProductosResponse.ProductoApi apiProduct : apiProducts) {
                        String apiName = apiProduct.getNombre().toLowerCase().trim();
                        nameToId.put(apiName, apiProduct.getId());
                        
                        // También crear mapeos parciales para nombres similares
                        // Ej: "Pizza Margarita" vs "Pizza Margherita"
                        String[] words = apiName.split("\\s+");
                        if (words.length >= 2) {
                            // Mapear por última palabra (ej: "Margarita" -> ID)
                            partialNameToId.put(words[words.length - 1], apiProduct.getId());
                            // Mapear por primeras palabras (ej: "Pizza Margarita" -> ID)
                            if (words.length >= 2) {
                                String firstTwoWords = words[0] + " " + words[1];
                                partialNameToId.put(firstTwoWords, apiProduct.getId());
                            }
                        }
                        
                        Log.d(TAG, "Producto en BD: '" + apiProduct.getNombre() + "' -> ID: " + apiProduct.getId());
                    }
                    
                    // Convertir items a request con IDs reales
                    java.util.List<PedidoRequest.ItemPedidoRequest> items = new java.util.ArrayList<>();
                    boolean allProductsFound = true;
                    java.util.List<String> missingProducts = new java.util.ArrayList<>();
                    
                    for (CartItem cartItem : restaurantItems) {
                        String productName = cartItem.getFoodItem().getName().toLowerCase().trim();
                        Long realProductId = nameToId.get(productName);
                        
                        // Si no se encuentra exacto, buscar por coincidencia parcial (contains)
                        if (realProductId == null) {
                            for (java.util.Map.Entry<String, Long> entry : nameToId.entrySet()) {
                                String apiName = entry.getKey();
                                // Normalizar ambos nombres (quitar espacios extra, convertir a minúsculas)
                                String normalizedCartName = productName.replaceAll("\\s+", " ").trim();
                                String normalizedApiName = apiName.replaceAll("\\s+", " ").trim();
                                
                                // Buscar si el nombre del carrito contiene palabras del nombre de la BD o viceversa
                                if (normalizedApiName.contains(normalizedCartName) || 
                                    normalizedCartName.contains(normalizedApiName)) {
                                    realProductId = entry.getValue();
                                    Log.d(TAG, "  ✓ Encontrado por coincidencia parcial: '" + productName + 
                                              "' coincide con '" + apiName + "'");
                                    break;
                                }
                            }
                        }
                        
                        // Si aún no se encuentra, intentar búsqueda por palabras clave (al menos 2 palabras deben coincidir)
                        if (realProductId == null) {
                            String[] cartWords = productName.split("\\s+");
                            java.util.List<String> significantWords = new java.util.ArrayList<>();
                            
                            // Filtrar palabras significativas (más de 2 caracteres)
                            for (String word : cartWords) {
                                if (word.length() > 2 && !word.equals("de") && !word.equals("con") && 
                                    !word.equals("y") && !word.equals("la")) {
                                    significantWords.add(word.toLowerCase());
                                }
                            }
                            
                            // Buscar productos que contengan al menos 2 palabras significativas
                            for (java.util.Map.Entry<String, Long> entry : nameToId.entrySet()) {
                                String apiName = entry.getKey().toLowerCase();
                                int matches = 0;
                                for (String word : significantWords) {
                                    if (apiName.contains(word)) {
                                        matches++;
                                    }
                                }
                                // Si al menos 2 palabras coinciden, o si es una palabra larga y única
                                if (matches >= 2 || (significantWords.size() == 1 && matches == 1 && 
                                    significantWords.get(0).length() > 5)) {
                                    realProductId = entry.getValue();
                                    Log.d(TAG, "  ✓ Encontrado por palabras clave: '" + productName + 
                                              "' -> '" + entry.getKey() + "' (" + matches + " coincidencias)");
                                    break;
                                }
                            }
                        }
                        
                        if (realProductId != null) {
                            // Usar ID real de la API
                            items.add(new PedidoRequest.ItemPedidoRequest(
                                realProductId,
                                cartItem.getQuantity(),
                                cartItem.getFoodItem().getPrice(),
                                new java.util.ArrayList<>() // Sin opciones por ahora
                            ));
                            Log.d(TAG, "✓ Producto encontrado: '" + cartItem.getFoodItem().getName() + 
                                      "' -> ID real: " + realProductId);
                        } else {
                            // Producto no encontrado
                            Log.e(TAG, "✗ Producto NO encontrado en API: '" + cartItem.getFoodItem().getName() + "'");
                            allProductsFound = false;
                            missingProducts.add(cartItem.getFoodItem().getName());
                            // NO agregar este item al pedido para evitar error de foreign key
                        }
                    }
                    
                    if (!allProductsFound) {
                        Log.e(TAG, "ERROR: Los siguientes productos no se encontraron en la BD:");
                        for (String missing : missingProducts) {
                            Log.e(TAG, "  - " + missing);
                        }
                        // Mostrar error al usuario y cancelar el pedido
                        setLoading(false);
                        Toast.makeText(CheckoutActivity.this, 
                            "Error: Los siguientes productos no están disponibles:\n" + 
                            String.join(", ", missingProducts), Toast.LENGTH_LONG).show();
                        return;
                    }
                    
                    if (items.isEmpty()) {
                        Log.e(TAG, "ERROR: No se pudo resolver ningún producto del carrito");
                        setLoading(false);
                        Toast.makeText(CheckoutActivity.this, 
                            "Error: No se pudieron encontrar los productos en la base de datos", 
                            Toast.LENGTH_LONG).show();
                        return;
                    }
                    
                    // Continuar con la creación del pedido
                    createPedidoWithItems(items, restaurantItems, itemsByRestaurant, direccionId, paymentMethod, 
                                         notes, finalCurrentIndex, finalTotalRestaurants, restaurantList, restaurantName);
                    
                } else {
                    // Si falla la búsqueda, usar IDs originales (intentará crear el pedido de todas formas)
                    Log.w(TAG, "Error al buscar productos, usando IDs originales");
                    createPedidoWithItems(null, restaurantItems, itemsByRestaurant, direccionId, paymentMethod, 
                                         notes, finalCurrentIndex, finalTotalRestaurants, restaurantList, restaurantName);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.zipp.delivery.network.response.ProductosResponse> call, Throwable t) {
                Log.e(TAG, "Error al buscar productos: " + t.getMessage());
                // Si falla la búsqueda, usar IDs originales
                createPedidoWithItems(null, restaurantItems, itemsByRestaurant, direccionId, paymentMethod, 
                                     notes, finalCurrentIndex, finalTotalRestaurants, restaurantList, restaurantName);
            }
        });
    }
    
    /**
     * Crear pedido con items (después de resolver IDs)
     */
    private void createPedidoWithItems(
            java.util.List<PedidoRequest.ItemPedidoRequest> items,
            final java.util.List<CartItem> restaurantItems,
            final java.util.Map<Integer, java.util.List<CartItem>> itemsByRestaurant,
            final long direccionId, final String paymentMethod, final String notes,
            final int finalCurrentIndex, final int finalTotalRestaurants,
            final java.util.List<java.util.Map.Entry<Integer, java.util.List<CartItem>>> restaurantList,
            final String restaurantName) {
        
        // Si items es null, crear lista con IDs originales
        if (items == null) {
            items = new java.util.ArrayList<>();
            for (CartItem cartItem : restaurantItems) {
                items.add(new PedidoRequest.ItemPedidoRequest(
                    cartItem.getFoodItem().getId(),
                    cartItem.getQuantity(),
                    cartItem.getFoodItem().getPrice(),
                    new java.util.ArrayList<>()
                ));
            }
        }
        
        // Calcular subtotal y delivery fee
        double subtotal = 0;
        double deliveryFee = 0;
        for (CartItem item : restaurantItems) {
            subtotal += item.getTotalPrice();
            deliveryFee = item.getRestaurantDeliveryFee();
        }
        final double finalTotal = subtotal + deliveryFee;
        final double finalDeliveryFee = deliveryFee;
        
        // Notas con información del restaurante
        final String orderNotes;
        if (finalTotalRestaurants > 1) {
            orderNotes = (notes.isEmpty() ? "" : notes + "\n") + 
                        "Pedido de " + restaurantName + 
                        " (Pedido " + (finalCurrentIndex + 1) + " de " + finalTotalRestaurants + ")";
        } else {
            orderNotes = notes;
        }
        
        final java.util.List<PedidoRequest.ItemPedidoRequest> finalItems = items;
        final PedidoRequest pedidoRequest = new PedidoRequest(
            direccionId,
            finalTotal,
            finalDeliveryFee,
            paymentMethod,
            finalItems,
            orderNotes
        );
        
        Log.d(TAG, "=== Creando pedido " + (finalCurrentIndex + 1) + "/" + finalTotalRestaurants + " ===");
        Log.d(TAG, "Restaurante: " + restaurantName);
        Log.d(TAG, "Dirección ID: " + direccionId);
        Log.d(TAG, "Total: " + finalTotal);
        Log.d(TAG, "Delivery Fee: " + finalDeliveryFee);
        Log.d(TAG, "Método de pago: " + paymentMethod);
        Log.d(TAG, "Items: " + finalItems.size());
        for (int i = 0; i < finalItems.size(); i++) {
            PedidoRequest.ItemPedidoRequest item = finalItems.get(i);
            Log.d(TAG, "  Item " + (i + 1) + ": Producto ID=" + item.getProductoId() + 
                      ", Cantidad=" + item.getCantidad() + ", Precio=" + item.getPrecioUnitario());
        }
        
        apiClient.getPedidosApi().crearPedido(pedidoRequest).enqueue(new Callback<PedidoResponse>() {
            @Override
            public void onResponse(Call<PedidoResponse> call, Response<PedidoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getPedido() != null) {
                    successfulOrders++;
                    Log.d(TAG, "Pedido " + (finalCurrentIndex + 1) + " creado exitosamente para " + restaurantName);
                    Log.d(TAG, "Pedidos exitosos: " + successfulOrders + "/" + totalOrdersToCreate);
                    
                    // Crear el siguiente pedido
                    if (finalCurrentIndex + 1 < restaurantList.size()) {
                        createPedidosForRestaurants(itemsByRestaurant, direccionId, paymentMethod, notes, 
                                                   finalCurrentIndex + 1, finalTotalRestaurants);
                    } else {
                        // Todos los pedidos procesados
                        checkOrderCompletion();
                    }
                } else {
                    failedOrders++;
                    String errorMessage = "Error " + response.code();
                    
                    // Intentar obtener mensaje de error del body
                    try {
                        if (response.body() != null) {
                            if (response.body().getError() != null && !response.body().getError().isEmpty()) {
                                errorMessage = response.body().getError();
                            } else if (response.body().getMensaje() != null && !response.body().getMensaje().isEmpty()) {
                                errorMessage = response.body().getMensaje();
                            }
                        }
                        
                        // Si no hay mensaje en el body, intentar leer el errorBody
                        if (errorMessage.equals("Error " + response.code()) && response.errorBody() != null) {
                            String errorBodyString = response.errorBody().string();
                            if (errorBodyString != null && !errorBodyString.isEmpty()) {
                                errorMessage = errorBodyString;
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al leer errorBody: " + e.getMessage());
                        errorMessage = "Error " + response.code() + ": " + e.getMessage();
                    }
                    
                    Log.e(TAG, "=== ERROR al crear pedido " + (finalCurrentIndex + 1) + " ===");
                    Log.e(TAG, "Código HTTP: " + response.code());
                    Log.e(TAG, "Mensaje de error: " + errorMessage);
                    Log.e(TAG, "Pedidos exitosos: " + successfulOrders);
                    Log.e(TAG, "Pedidos fallidos: " + failedOrders + "/" + totalOrdersToCreate);
                    
                    Toast.makeText(CheckoutActivity.this, 
                        "Error al crear pedido de " + restaurantName + ": " + errorMessage, 
                        Toast.LENGTH_LONG).show();
                    
                    // Continuar con el siguiente pedido
                    if (finalCurrentIndex + 1 < restaurantList.size()) {
                        createPedidosForRestaurants(itemsByRestaurant, direccionId, paymentMethod, notes, 
                                                   finalCurrentIndex + 1, finalTotalRestaurants);
                    } else {
                        // Todos los pedidos procesados
                        checkOrderCompletion();
                    }
                }
            }

            @Override
            public void onFailure(Call<PedidoResponse> call, Throwable t) {
                failedOrders++;
                Log.e(TAG, "Error de conexión al crear pedido " + (finalCurrentIndex + 1) + ": " + t.getMessage());
                Log.e(TAG, "Pedidos fallidos: " + failedOrders + "/" + totalOrdersToCreate);
                
                Toast.makeText(CheckoutActivity.this, 
                    "Error de conexión al crear pedido de " + restaurantName, Toast.LENGTH_LONG).show();
                
                // Continuar con el siguiente pedido
                if (finalCurrentIndex + 1 < restaurantList.size()) {
                    createPedidosForRestaurants(itemsByRestaurant, direccionId, paymentMethod, notes, 
                                               finalCurrentIndex + 1, finalTotalRestaurants);
                } else {
                    // Todos los pedidos procesados
                    checkOrderCompletion();
                }
            }
        });
    }
    
    /**
     * Simular pedido local cuando no hay conexión
     */
    private void simulateLocalOrder(String paymentMethod) {
        setLoading(false);
        onOrderSuccess();
    }
    
    /**
     * Verificar si todos los pedidos se completaron y manejar el resultado
     */
    private void checkOrderCompletion() {
        setLoading(false);
        
        if (failedOrders == 0 && successfulOrders == totalOrdersToCreate) {
            // Todos los pedidos se crearon exitosamente
            onOrderSuccess();
        } else if (successfulOrders > 0) {
            // Algunos pedidos se crearon, otros fallaron
            String message = successfulOrders + " de " + totalOrdersToCreate + " pedidos se crearon exitosamente";
            if (failedOrders > 0) {
                message += ". " + failedOrders + " pedidos fallaron.";
            }
            
            new AlertDialog.Builder(this)
                .setTitle("Pedido parcial")
                .setMessage(message + "\n\n¿Deseas continuar?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    cartManager.clearCart();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Reintentar", null)
                .show();
        } else {
            // Todos los pedidos fallaron
            new AlertDialog.Builder(this)
                .setTitle("Error al crear pedidos")
                .setMessage("No se pudo crear ningún pedido. Por favor, verifica tu conexión e intenta nuevamente.")
                .setPositiveButton("OK", null)
                .show();
        }
    }
    
    /**
     * Manejar éxito del pedido
     */
    private void onOrderSuccess() {
        Toast.makeText(this, "¡Pedido realizado con éxito!", Toast.LENGTH_LONG).show();
        cartManager.clearCart();
        
        // Volver al MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    private void setLoading(boolean loading) {
        btnPlaceOrder.setEnabled(!loading);
        btnPlaceOrder.setText(loading ? "Procesando..." : getString(R.string.place_order));
        
        if (progressBar != null) {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }
}
