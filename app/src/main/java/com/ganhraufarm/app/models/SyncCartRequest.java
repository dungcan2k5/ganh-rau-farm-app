package com.ganhraufarm.app.models;

import com.google.gson.annotations.SerializedName;

public class SyncCartRequest {
    public static class CartItemBody {
        @SerializedName("user_id")
        private String userId;

        @SerializedName("product_id")
        private int productId;

        @SerializedName("quantity")
        private int quantity;

        public CartItemBody(String userId, int productId, int quantity) {
            this.userId = userId;
            this.productId = productId;
            this.quantity = quantity;
        }
    }
}
