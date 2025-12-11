package com.zipp.delivery.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    
    public enum Status {
        PENDING("Pendiente"),
        CONFIRMED("Confirmado"),
        PREPARING("Preparando"),
        ON_THE_WAY("En camino"),
        DELIVERED("Entregado"),
        CANCELLED("Cancelado");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private int id;
    private String orderNumber;
    private List<CartItem> items;
    private double subtotal;
    private double deliveryFee;
    private double total;
    private Status status;
    private Date orderDate;
    private Address deliveryAddress;
    private String paymentMethod;
    private String restaurantName;
    private int restaurantId;
    private String estimatedDeliveryTime;

    public Order() {
        this.status = Status.PENDING;
        this.orderDate = new Date();
    }

    // Getters
    public int getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public List<CartItem> getItems() { return items; }
    public double getSubtotal() { return subtotal; }
    public double getDeliveryFee() { return deliveryFee; }
    public double getTotal() { return total; }
    public Status getStatus() { return status; }
    public Date getOrderDate() { return orderDate; }
    public Address getDeliveryAddress() { return deliveryAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getRestaurantName() { return restaurantName; }
    public int getRestaurantId() { return restaurantId; }
    public String getEstimatedDeliveryTime() { return estimatedDeliveryTime; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public void setItems(List<CartItem> items) { this.items = items; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }
    public void setTotal(double total) { this.total = total; }
    public void setStatus(Status status) { this.status = status; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public void setDeliveryAddress(Address deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }
    public void setEstimatedDeliveryTime(String estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }

    public String getFormattedTotal() {
        return String.format("S/ %.2f", total);
    }

    public int getItemCount() {
        if (items == null) return 0;
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }
}




