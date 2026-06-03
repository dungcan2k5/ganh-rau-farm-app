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

    @SerializedName("category")
    private String category;

    @SerializedName("categories")
    private CategoryNested categoryNested;

    public static class CategoryNested {
        @SerializedName("name")
        private String name;
        public String getName() { return name; }
    }

    @SerializedName("shop_name")
    private String shopName;

    @SerializedName("rating")
    private float rating;

    @SerializedName("old_price")
    private Double oldPrice;

    public int getId() { return id; }
    public int getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getImageUrl() { return imageUrl; }
    public String getUnit() { return unit; }
    public boolean isActive() { return isActive; }
    public String getCategory() { 
        if (categoryNested != null) return categoryNested.getName();
        return category; 
    }
    public String getShopName() { return shopName; }
    public float getRating() { return rating; }
    public Double getOldPrice() { return oldPrice; }
}
