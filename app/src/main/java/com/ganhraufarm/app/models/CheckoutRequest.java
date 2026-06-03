package com.ganhraufarm.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CheckoutRequest {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("coupon_code")
    private String couponCode;

    @SerializedName("shipping_address")
    private String shippingAddress;

    @SerializedName("receiver_phone")
    private String receiverPhone;

    @SerializedName("items")
    private List<CheckoutItem> items;

    public CheckoutRequest(String userId, String couponCode, String shippingAddress, String receiverPhone, List<CheckoutItem> items) {
        this.userId = userId;
        this.couponCode = couponCode;
        this.shippingAddress = shippingAddress;
        this.receiverPhone = receiverPhone;
        this.items = items;
    }

    public static class CheckoutItem {
        @SerializedName("product_id")
        private int productId;
        
        @SerializedName("quantity")
        private int quantity;

        public CheckoutItem(int productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }

    // Getters
    public String getUserId() { return userId; }
    public String getCouponCode() { return couponCode; }
    public String getShippingAddress() { return shippingAddress; }
    public String getReceiverPhone() { return receiverPhone; }
    public List<CheckoutItem> getItems() { return items; }
}
