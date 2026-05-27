package com.ganhraufarm.app.models;

import com.google.gson.annotations.SerializedName;

public class AuthRequest {
    @SerializedName("provider")
    private String provider;
    
    @SerializedName("id_token")
    private String idToken;

    public AuthRequest(String provider, String idToken) {
        this.provider = provider;
        this.idToken = idToken;
    }
}
