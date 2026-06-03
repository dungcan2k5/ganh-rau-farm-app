package com.ganhraufarm.app.api;

import com.ganhraufarm.app.models.Address;
import com.ganhraufarm.app.models.AuthRequest;
import com.ganhraufarm.app.models.AuthResponse;
import com.ganhraufarm.app.models.Category;
import com.ganhraufarm.app.models.CheckoutRequest;
import com.ganhraufarm.app.models.CheckoutResponse;
import com.ganhraufarm.app.models.Coupon;
import com.ganhraufarm.app.models.Order;
import com.ganhraufarm.app.models.Product;
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

    @GET("rest/v1/categories")
    Call<List<Category>> getCategories(@Query("select") String select);

    @GET("rest/v1/products")
    Call<List<Product>> getProducts(@Query("select") String select, @Query("is_active") String activeFilter);

    @GET("rest/v1/products")
    Call<List<Product>> getProductById(@Query("id") String idFilter, @Query("select") String select);

    @GET("rest/v1/addresses")
    Call<List<Address>> getAddresses(@Query("user_id") String userIdFilter, @Query("select") String select);

    @GET("rest/v1/orders")
    Call<List<Order>> getOrders(@Query("user_id") String userIdFilter, @Query("select") String select);

    @GET("rest/v1/orders")
    Call<List<Order>> getOrderDetails(@Query("id") String idFilter, @Query("select") String select);

    @GET("rest/v1/coupons")
    Call<List<Coupon>> getCoupons(@Query("select") String select, @Query("is_active") String activeFilter);

    @POST("functions/v1/process-payment")
    Call<CheckoutResponse> processPayment(@Body CheckoutRequest request);
}
