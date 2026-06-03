package com.ganhraufarm.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ganhraufarm.app.adapter.CategoryAdapter;
import com.ganhraufarm.app.adapter.CouponAdapter;
import com.ganhraufarm.app.adapter.ProductAdapter;
import com.ganhraufarm.app.api.RetrofitClient;
import com.ganhraufarm.app.databinding.ActivityHomeBinding;
import com.ganhraufarm.app.models.Category;
import com.ganhraufarm.app.models.Coupon;
import com.ganhraufarm.app.models.Product;
import com.ganhraufarm.app.models.UserProfile;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerViews();

        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        String token = prefs.getString("access_token", null);
        String userId = prefs.getString("user_id", null);

        if (token != null && userId != null) {
            RetrofitClient.setAuthToken(token);
            fetchUserProfile(userId);
            fetchCategories();
            fetchCoupons();
            fetchProducts();
        } else {
            handleLogout();
        }

        binding.navCartContainer.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        binding.btnAccount.setOnClickListener(v -> startActivity(new Intent(this, AccountActivity.class)));
        binding.btnOrder.setOnClickListener(v -> startActivity(new Intent(this, CheckoutActivity.class)));
    }

    private void setupRecyclerViews() {
        binding.recyclerView1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerView1.setNestedScrollingEnabled(false);
        
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerView2.setNestedScrollingEnabled(false);
        
        binding.recyclerView3.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerView3.setNestedScrollingEnabled(false);
    }

    private void fetchCategories() {
        RetrofitClient.getApi().getCategories("*").enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    binding.recyclerView1.setAdapter(new CategoryAdapter(response.body()));
                } else if (response.code() == 401) {
                    handleLogout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                Log.e(TAG, "Categories failed", t);
            }
        });
    }

    private void fetchCoupons() {
        RetrofitClient.getApi().getCoupons("*", "eq.true").enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(@NonNull Call<List<Coupon>> call, @NonNull Response<List<Coupon>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Coupons fetched: " + response.body().size());
                    binding.recyclerView2.setAdapter(new CouponAdapter(response.body()));
                } else {
                    Log.e(TAG, "Coupons error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Coupon>> call, @NonNull Throwable t) {
                Log.e(TAG, "Coupons failed", t);
            }
        });
    }

    private void fetchProducts() {
        RetrofitClient.getApi().getProducts("*", "eq.true").enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Products fetched: " + response.body().size());
                    binding.recyclerView3.setAdapter(new ProductAdapter(response.body(), product -> {
                        CartManager.getInstance(HomeActivity.this).addItem(product);
                        Toast.makeText(HomeActivity.this, "Đã thêm " + product.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        updateCartTotal();
                    }));
                } else if (response.code() == 401) {
                    handleLogout();
                } else {
                    Log.e(TAG, "Products error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                Log.e(TAG, "Products failed", t);
            }
        });
    }

    private void fetchUserProfile(String userId) {
        RetrofitClient.getApi().getUserProfile("eq." + userId, "*").enqueue(new Callback<List<UserProfile>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserProfile>> call, @NonNull Response<List<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Log.d(TAG, "Profile loaded: " + response.body().get(0).getFullName());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserProfile>> call, @NonNull Throwable t) {
                Log.e(TAG, "Fetch failed", t);
                Toast.makeText(HomeActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartTotal();
    }

    private void updateCartTotal() {
        long total = CartManager.getInstance(this).getTotalPrice();
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        binding.tvHomeCartTotal.setText(formatter.format(total) + "đ");
    }

    private void handleLogout() {
        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
