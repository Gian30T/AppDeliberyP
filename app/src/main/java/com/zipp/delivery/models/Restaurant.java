package com.zipp.delivery.models;

import java.io.Serializable;
import java.util.List;

public class Restaurant implements Serializable {
    private int id;
    private String name;
    private String description;
    private String imageUrl;
    private String category;
    private double rating;
    private int reviewCount;
    private String deliveryTime;
    private double deliveryFee;
    private double minOrder;
    private String address;
    private boolean isFavorite;
    private boolean isOpen;
    private List<FoodItem> menu;

    public Restaurant() {}

    public Restaurant(int id, String name, String description, String imageUrl, String category,
                      double rating, int reviewCount, String deliveryTime, double deliveryFee,
                      double minOrder, String address) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.deliveryTime = deliveryTime;
        this.deliveryFee = deliveryFee;
        this.minOrder = minOrder;
        this.address = address;
        this.isFavorite = false;
        this.isOpen = true;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
    public String getDeliveryTime() { return deliveryTime; }
    public double getDeliveryFee() { return deliveryFee; }
    public double getMinOrder() { return minOrder; }
    public String getAddress() { return address; }
    public boolean isFavorite() { return isFavorite; }
    public boolean isOpen() { return isOpen; }
    public List<FoodItem> getMenu() { return menu; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCategory(String category) { this.category = category; }
    public void setRating(double rating) { this.rating = rating; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public void setDeliveryTime(String deliveryTime) { this.deliveryTime = deliveryTime; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }
    public void setMinOrder(double minOrder) { this.minOrder = minOrder; }
    public void setAddress(String address) { this.address = address; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public void setOpen(boolean open) { isOpen = open; }
    public void setMenu(List<FoodItem> menu) { this.menu = menu; }

    public String getFormattedDeliveryFee() {
        if (deliveryFee == 0) {
            return "Gratis";
        }
        return String.format("S/ %.2f", deliveryFee);
    }

    public String getFormattedMinOrder() {
        return String.format("S/ %.2f", minOrder);
    }
}




