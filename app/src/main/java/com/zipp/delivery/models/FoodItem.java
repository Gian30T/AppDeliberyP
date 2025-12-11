package com.zipp.delivery.models;

import java.io.Serializable;
import java.util.List;

public class FoodItem implements Serializable {
    private int id;
    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private String category;
    private boolean isPopular;
    private boolean isAvailable;
    private int restaurantId;
    private List<String> ingredients;
    private int calories;
    private int preparationTime;

    public FoodItem() {}

    public FoodItem(int id, String name, String description, String imageUrl, double price,
                    String category, int restaurantId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.category = category;
        this.restaurantId = restaurantId;
        this.isPopular = false;
        this.isAvailable = true;
    }

    public FoodItem(int id, String name, String description, String imageUrl, double price,
                    String category, int restaurantId, boolean isPopular) {
        this(id, name, description, imageUrl, price, category, restaurantId);
        this.isPopular = isPopular;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public boolean isPopular() { return isPopular; }
    public boolean isAvailable() { return isAvailable; }
    public int getRestaurantId() { return restaurantId; }
    public List<String> getIngredients() { return ingredients; }
    public int getCalories() { return calories; }
    public int getPreparationTime() { return preparationTime; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setPopular(boolean popular) { isPopular = popular; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
    public void setCalories(int calories) { this.calories = calories; }
    public void setPreparationTime(int preparationTime) { this.preparationTime = preparationTime; }

    public String getFormattedPrice() {
        return String.format("S/ %.2f", price);
    }
}




