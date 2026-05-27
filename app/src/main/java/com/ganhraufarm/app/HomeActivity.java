package com.ganhraufarm.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ganhraufarm.app.api.RetrofitClient;
import com.ganhraufarm.app.databinding.ActivityHomeBinding;
import com.ganhraufarm.app.models.UserProfile;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        String token = prefs.getString("access_token", null);
        String userId = prefs.getString("user_id", null);

        if (token != null && userId != null) {
            RetrofitClient.setAuthToken(token);
            fetchUserProfile(userId);
        } else {
            handleLogout();
        }

        binding.navCartContainer.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        binding.btnAccount.setOnClickListener(v -> startActivity(new Intent(this, AccountActivity.class)));
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

    private void handleLogout() {
        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
