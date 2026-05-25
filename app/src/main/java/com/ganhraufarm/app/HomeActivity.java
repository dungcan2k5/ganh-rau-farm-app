package com.ganhraufarm.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
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

        binding.btnLogout.setOnClickListener(v -> handleLogout());
    }

    private void fetchUserProfile(String userId) {
        RetrofitClient.getApi().getUserProfile("eq." + userId, "*").enqueue(new Callback<List<UserProfile>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserProfile>> call, @NonNull Response<List<UserProfile>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    displayData(response.body().get(0));
                } else {
                    Log.e(TAG, "Profile fetch failed: " + response.code());
                    Toast.makeText(HomeActivity.this, "Không tìm thấy hồ sơ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserProfile>> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Network error", t);
                Toast.makeText(HomeActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData(UserProfile profile) {
        binding.layoutContent.setVisibility(View.VISIBLE);
        
        binding.tvFullName.setText(profile.getFullName() != null ? profile.getFullName() : "Chưa cập nhật");
        binding.tvEmail.setText(profile.getEmail() != null ? profile.getEmail() : "N/A");
        binding.tvPhone.setText(profile.getPhone() != null ? profile.getPhone() : "Chưa cập nhật");
        binding.tvAddress.setText(profile.getAddress() != null ? profile.getAddress() : "Chưa cập nhật");
        binding.tvRole.setText(profile.getRole() != null ? profile.getRole() : "customer");
    }

    private void handleLogout() {
        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
