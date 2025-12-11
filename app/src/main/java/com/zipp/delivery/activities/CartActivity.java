package com.zipp.delivery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zipp.delivery.R;
import com.zipp.delivery.adapters.CartAdapter;
import com.zipp.delivery.models.CartItem;
import com.zipp.delivery.utils.CartManager;

import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartManager.CartUpdateListener {

    private ImageView ivBack;
    private TextView tvTitle, tvRestaurantName;
    private RecyclerView rvCart;
    private LinearLayout emptyState, cartContent;
    private TextView tvSubtotal, tvDelivery, tvTotal;
    private Button btnCheckout, btnExplore;

    private CartManager cartManager;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartManager = CartManager.getInstance();
        cartManager.addListener(this);

        initViews();
        setupRecyclerView();
        setupClickListeners();
        updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cartManager.removeListener(this);
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvRestaurantName = findViewById(R.id.tv_restaurant_name);
        rvCart = findViewById(R.id.rv_cart);
        emptyState = findViewById(R.id.empty_state);
        cartContent = findViewById(R.id.cart_content);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvDelivery = findViewById(R.id.tv_delivery);
        tvTotal = findViewById(R.id.tv_total);
        btnCheckout = findViewById(R.id.btn_checkout);
        btnExplore = findViewById(R.id.btn_explore);
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(
            cartManager.getCartItems(),
            this::onIncrement,
            this::onDecrement,
            this::onRemove
        );
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(cartAdapter);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        btnCheckout.setOnClickListener(v -> {
            startActivity(new Intent(this, CheckoutActivity.class));
        });

        btnExplore.setOnClickListener(v -> {
            finish();
        });
    }

    private void onIncrement(CartItem item) {
        cartManager.incrementQuantity(item.getFoodItem());
    }

    private void onDecrement(CartItem item) {
        cartManager.decrementQuantity(item.getFoodItem());
    }

    private void onRemove(CartItem item) {
        cartManager.removeItem(item.getFoodItem());
    }

    private void updateUI() {
        List<CartItem> items = cartManager.getCartItems();

        if (items.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            cartContent.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            cartContent.setVisibility(View.VISIBLE);

            // Mostrar información de restaurantes
            Map<Integer, List<CartItem>> itemsByRestaurant = cartManager.getItemsByRestaurant();
            
            if (itemsByRestaurant.size() == 1) {
                // Un solo restaurante
                String restaurantName = cartManager.getCurrentRestaurantName();
                if (restaurantName != null) {
                    tvRestaurantName.setText(restaurantName);
                    tvRestaurantName.setVisibility(View.VISIBLE);
                } else {
                    tvRestaurantName.setVisibility(View.GONE);
                }
            } else {
                // Múltiples restaurantes
                tvRestaurantName.setText(itemsByRestaurant.size() + " restaurantes");
                tvRestaurantName.setVisibility(View.VISIBLE);
            }

            cartAdapter.updateItems(items);

            tvSubtotal.setText(cartManager.getFormattedSubtotal());
            tvDelivery.setText(cartManager.getFormattedDeliveryFee());
            tvTotal.setText(cartManager.getFormattedTotal());
        }
    }

    @Override
    public void onCartUpdated(int itemCount, double total) {
        runOnUiThread(this::updateUI);
    }
}



