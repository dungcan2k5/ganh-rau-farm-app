package com.ganhraufarm.app.models;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("id")
    private int id;
    
    @SerializedName("category_id")
    private int categoryId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("price")
    private double price;
    
    @SerializedName("stock")
    private int stock;
    
    @SerializedName("image_url")
    private String imageUrl;
    
    @SerializedName("unit")
    private String unit;
    
    @SerializedName("is_active")
    private boolean isActive;

    public int getId() { return id; }
    public int getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getImageUrl() { return imageUrl; }
    public String getUnit() { return unit; }
    public boolean isActive() { return isActive; }
}
