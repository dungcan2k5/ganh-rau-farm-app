package com.ganhraufarm.app.models;

public class CartItem {
    private String id;
    private String productId;
    private String productName;
    private long price;
    private int quantity;
    private String imageUrl;
    private boolean isSelected;

    public CartItem(String id, String productId, String productName, long price, int quantity, String imageUrl) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.isSelected = true;
    }

    public String getId() { return id; }
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public long getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }
    public boolean isSelected() { return isSelected; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setSelected(boolean selected) { isSelected = selected; }
}