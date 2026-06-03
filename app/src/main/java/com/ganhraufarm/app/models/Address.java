package com.ganhraufarm.app.models;

import com.google.gson.annotations.SerializedName;

public class Address {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("recipient_name")
    private String recipientName;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("detail_address")
    private String detailAddress;

    @SerializedName("is_default")
    private boolean isDefault;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDetailAddress() { return detailAddress; }
    public void setDetailAddress(String detailAddress) { this.detailAddress = detailAddress; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}
