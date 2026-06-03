package com.ganhraufarm.app.models;

import com.google.gson.annotations.SerializedName;

public class CheckoutRequest {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("coupon_code")
    private String couponCode;

    @SerializedName("shipping_address")
    private String shippingAddress;

    @SerializedName("receiver_phone")
    private String receiverPhone;

    public CheckoutRequest(String userId, String couponCode, String shippingAddress, String receiverPhone) {
        this.userId = userId;
        this.couponCode = couponCode;
        this.shippingAddress = shippingAddress;
        this.receiverPhone = receiverPhone;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getCouponCode() { return couponCode; }
    public String getShippingAddress() { return shippingAddress; }
    public String getReceiverPhone() { return receiverPhone; }
}
