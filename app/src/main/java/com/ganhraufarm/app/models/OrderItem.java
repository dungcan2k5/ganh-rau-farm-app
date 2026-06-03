package com.ganhraufarm.app.models;

import com.google.gson.annotations.SerializedName;

public class OrderItem {
    @SerializedName("id")
    private int id;

    @SerializedName("order_id")
    private int orderId;

    @SerializedName("product_id")
    private int productId;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("price")
    private double price;

    // Optional: Include product details directly if Supabase query uses joins
    @SerializedName("product")
    private Product product;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
