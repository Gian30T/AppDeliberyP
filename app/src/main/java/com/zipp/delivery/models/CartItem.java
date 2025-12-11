package com.zipp.delivery.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private FoodItem foodItem;
    private int quantity;
    private String specialInstructions;
    private int restaurantId;
    private String restaurantName;
    private double restaurantDeliveryFee;

    public CartItem() {}

    public CartItem(FoodItem foodItem, int quantity) {
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.restaurantId = foodItem.getRestaurantId();
    }

    public CartItem(FoodItem foodItem, int quantity, String restaurantName) {
        this(foodItem, quantity);
        this.restaurantName = restaurantName;
    }

    public CartItem(FoodItem foodItem, int quantity, String restaurantName, double restaurantDeliveryFee) {
        this(foodItem, quantity, restaurantName);
        this.restaurantDeliveryFee = restaurantDeliveryFee;
    }

    // Getters
    public FoodItem getFoodItem() { return foodItem; }
    public int getQuantity() { return quantity; }
    public String getSpecialInstructions() { return specialInstructions; }
    public int getRestaurantId() { return restaurantId; }
    public String getRestaurantName() { return restaurantName; }
    public double getRestaurantDeliveryFee() { return restaurantDeliveryFee; }

    // Setters
    public void setFoodItem(FoodItem foodItem) { this.foodItem = foodItem; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public void setRestaurantDeliveryFee(double restaurantDeliveryFee) { this.restaurantDeliveryFee = restaurantDeliveryFee; }

    public void incrementQuantity() {
        this.quantity++;
    }

    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }

    public double getTotalPrice() {
        return foodItem.getPrice() * quantity;
    }

    public String getFormattedTotalPrice() {
        return String.format("S/ %.2f", getTotalPrice());
    }
}




