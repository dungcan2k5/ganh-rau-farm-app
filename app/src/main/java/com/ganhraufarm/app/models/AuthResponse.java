package com.ganhraufarm.app.models;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("user")
    private UserInfo user;

    public String getAccessToken() {
        return accessToken;
    }

    public UserInfo getUser() {
        return user;
    }

    public static class UserInfo {
        @SerializedName("id")
        private String id;

        public String getId() {
            return id;
        }
    }
}
