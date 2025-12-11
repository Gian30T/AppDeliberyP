package com.zipp.delivery.models;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String name;
    private int iconResId;
    private int colorResId;

    public Category() {}

    public Category(int id, String name, int iconResId, int colorResId) {
        this.id = id;
        this.name = name;
        this.iconResId = iconResId;
        this.colorResId = colorResId;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getIconResId() { return iconResId; }
    public int getColorResId() { return colorResId; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }
    public void setColorResId(int colorResId) { this.colorResId = colorResId; }
}




