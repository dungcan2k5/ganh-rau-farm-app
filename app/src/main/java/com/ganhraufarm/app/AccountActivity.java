package com.ganhraufarm.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ganhraufarm.app.api.RetrofitClient;
import com.ganhraufarm.app.models.UserProfile;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";
    private TextView tvUserName, tvUserEmail, tvUserPhone, tvUserAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initViews();
        checkSession();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvUserAddress = findViewById(R.id.tvUserAddress);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnLogout).setOnClickListener(v -> handleLogout());
    }

    private void checkSession() {
        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        String token = prefs.getString("access_token", null);
        String userId = prefs.getString("user_id", null);

        if (token != null && userId != null) {
            RetrofitClient.setAuthToken(token);
            fetchUserProfile(userId);
        } else {
            handleLogout();
        }
    }

    private void fetchUserProfile(String userId) {
        RetrofitClient.getApi().getUserProfile("eq." + userId, "*").enqueue(new Callback<List<UserProfile>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserProfile>> call, @NonNull Response<List<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    displayData(response.body().get(0));
                } else {
                    Toast.makeText(AccountActivity.this, "Không thể tải hồ sơ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserProfile>> call, @NonNull Throwable t) {
                Log.e(TAG, "Fetch failed", t);
                Toast.makeText(AccountActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData(UserProfile profile) {
        tvUserName.setText(profile.getFullName() != null ? profile.getFullName() : "N/A");
        tvUserEmail.setText(profile.getEmail() != null ? profile.getEmail() : "N/A");
        tvUserPhone.setText(profile.getPhone() != null ? profile.getPhone() : "Chưa cập nhật");
        tvUserAddress.setText(profile.getAddress() != null ? profile.getAddress() : "Chưa cập nhật");
    }

    private void handleLogout() {
        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
