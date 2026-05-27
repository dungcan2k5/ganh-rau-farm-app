package com.ganhraufarm.app.api;

import com.ganhraufarm.app.models.AuthRequest;
import com.ganhraufarm.app.models.AuthResponse;
import com.ganhraufarm.app.models.UserProfile;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseApi {
    
    @POST("auth/v1/token?grant_type=id_token")
    Call<AuthResponse> exchangeToken(@Body AuthRequest request);

    @GET("rest/v1/users")
    Call<List<UserProfile>> getUserProfile(@Query("id") String idFilter, @Query("select") String select);
}
