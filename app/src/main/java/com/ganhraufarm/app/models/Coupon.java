package com.ganhraufarm.app.models;

import com.google.gson.annotations.SerializedName;

public class Coupon {
    @SerializedName("id")
    private int id;
    
    @SerializedName("code")
    private String code;
    
    @SerializedName("discount_type")
    private String discountType;
    
    @SerializedName("discount_value")
    private double discountValue;
    
    @SerializedName("min_order_value")
    private double minOrderValue;
    
    @SerializedName("valid_until")
    private String validUntil;
    
    @SerializedName("is_active")
    private boolean isActive;

    public int getId() { return id; }
    public String getCode() { return code; }
    public String getDiscountType() { return discountType; }
    public double getDiscountValue() { return discountValue; }
    public double getMinOrderValue() { return minOrderValue; }
    public String getValidUntil() { return validUntil; }
    public boolean isActive() { return isActive; }
}
