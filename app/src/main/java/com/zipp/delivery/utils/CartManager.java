package com.zipp.delivery.utils;

import com.zipp.delivery.models.CartItem;
import com.zipp.delivery.models.FoodItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;
    private String currentRestaurantName;
    private int currentRestaurantId;
    private double deliveryFee;
    private List<CartUpdateListener> listeners;

    public interface CartUpdateListener {
        void onCartUpdated(int itemCount, double total);
    }

    private CartManager() {
        cartItems = new ArrayList<>();
        listeners = new ArrayList<>();
        deliveryFee = 0;
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addListener(CartUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(CartUpdateListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (CartUpdateListener listener : listeners) {
            listener.onCartUpdated(getTotalItemCount(), getTotal());
        }
    }

    public void addItem(FoodItem foodItem, String restaurantName) {
        addItem(foodItem, restaurantName, 0.0);
    }

    public void addItem(FoodItem foodItem, String restaurantName, double restaurantDeliveryFee) {
        // Check if item already exists
        for (CartItem cartItem : cartItems) {
            if (cartItem.getFoodItem().getId() == foodItem.getId()) {
                cartItem.incrementQuantity();
                notifyListeners();
                return;
            }
        }

        // Update current restaurant info if cart is empty
        if (cartItems.isEmpty()) {
            currentRestaurantName = restaurantName;
            currentRestaurantId = foodItem.getRestaurantId();
        }

        CartItem newItem = new CartItem(foodItem, 1, restaurantName, restaurantDeliveryFee);
        cartItems.add(newItem);
        
        // Recalcular delivery fee total
        recalculateDeliveryFee();
        notifyListeners();
    }
    
    /**
     * Recalcula el delivery fee total sumando los delivery fees únicos de cada restaurante
     */
    private void recalculateDeliveryFee() {
        double totalDeliveryFee = 0;
        Set<Integer> processedRestaurants = new HashSet<>();
        
        for (CartItem item : cartItems) {
            int restaurantId = item.getRestaurantId();
            // Solo agregar el delivery fee una vez por restaurante
            if (!processedRestaurants.contains(restaurantId)) {
                totalDeliveryFee += item.getRestaurantDeliveryFee();
                processedRestaurants.add(restaurantId);
            }
        }
        
        this.deliveryFee = totalDeliveryFee;
    }

    public void removeItem(FoodItem foodItem) {
        CartItem toRemove = null;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getFoodItem().getId() == foodItem.getId()) {
                toRemove = cartItem;
                break;
            }
        }
        if (toRemove != null) {
            cartItems.remove(toRemove);
            
            // Recalcular delivery fee
            recalculateDeliveryFee();
            
            if (cartItems.isEmpty()) {
                currentRestaurantName = null;
                currentRestaurantId = 0;
                deliveryFee = 0;
            } else {
                // Actualizar currentRestaurantId al primer restaurante que quede
                currentRestaurantId = cartItems.get(0).getRestaurantId();
                currentRestaurantName = cartItems.get(0).getRestaurantName();
            }
            notifyListeners();
        }
    }

    public void updateQuantity(FoodItem foodItem, int quantity) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getFoodItem().getId() == foodItem.getId()) {
                if (quantity <= 0) {
                    removeItem(foodItem);
                } else {
                    cartItem.setQuantity(quantity);
                    notifyListeners();
                }
                return;
            }
        }
    }

    public void incrementQuantity(FoodItem foodItem) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getFoodItem().getId() == foodItem.getId()) {
                cartItem.incrementQuantity();
                notifyListeners();
                return;
            }
        }
    }

    public void decrementQuantity(FoodItem foodItem) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getFoodItem().getId() == foodItem.getId()) {
                if (cartItem.getQuantity() > 1) {
                    cartItem.decrementQuantity();
                } else {
                    removeItem(foodItem);
                }
                notifyListeners();
                return;
            }
        }
    }

    public void clearCart() {
        cartItems.clear();
        currentRestaurantName = null;
        currentRestaurantId = 0;
        deliveryFee = 0;
        notifyListeners();
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public int getTotalItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }

    public double getSubtotal() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getTotalPrice();
        }
        return subtotal;
    }

    public double getDeliveryFee() {
        // Recalcular por si acaso
        recalculateDeliveryFee();
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        // Este método se mantiene para compatibilidad, pero se recalcula automáticamente
        this.deliveryFee = deliveryFee;
    }
    
    /**
     * Obtener items agrupados por restaurante
     */
    public Map<Integer, List<CartItem>> getItemsByRestaurant() {
        Map<Integer, List<CartItem>> grouped = new HashMap<>();
        for (CartItem item : cartItems) {
            int restaurantId = item.getRestaurantId();
            if (!grouped.containsKey(restaurantId)) {
                grouped.put(restaurantId, new ArrayList<>());
            }
            grouped.get(restaurantId).add(item);
        }
        return grouped;
    }
    
    /**
     * Obtener el delivery fee para un restaurante específico
     */
    public double getDeliveryFeeForRestaurant(int restaurantId) {
        for (CartItem item : cartItems) {
            if (item.getRestaurantId() == restaurantId) {
                return item.getRestaurantDeliveryFee();
            }
        }
        return 0.0;
    }

    public double getTotal() {
        return getSubtotal() + deliveryFee;
    }

    public String getFormattedSubtotal() {
        return String.format("S/ %.2f", getSubtotal());
    }

    public String getFormattedDeliveryFee() {
        if (deliveryFee == 0) {
            return "Gratis";
        }
        return String.format("S/ %.2f", deliveryFee);
    }

    public String getFormattedTotal() {
        return String.format("S/ %.2f", getTotal());
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    public String getCurrentRestaurantName() {
        return currentRestaurantName;
    }

    public int getCurrentRestaurantId() {
        return currentRestaurantId;
    }

    public boolean isFromDifferentRestaurant(int restaurantId) {
        return !cartItems.isEmpty() && currentRestaurantId != restaurantId;
    }

    public int getItemQuantity(int foodItemId) {
        for (CartItem item : cartItems) {
            if (item.getFoodItem().getId() == foodItemId) {
                return item.getQuantity();
            }
        }
        return 0;
    }
}




