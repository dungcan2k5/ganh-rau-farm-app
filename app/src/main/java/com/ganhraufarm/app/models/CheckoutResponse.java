package com.ganhraufarm.app.models;

import com.google.gson.annotations.SerializedName;

public class CheckoutResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("order_id")
    private int orderId;

    @SerializedName("total_amount")
    private long totalAmount;

    @SerializedName("final_amount")
    private long finalAmount;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public int getOrderId() { return orderId; }
    public long getTotalAmount() { return totalAmount; }
    public long getFinalAmount() { return finalAmount; }
}
