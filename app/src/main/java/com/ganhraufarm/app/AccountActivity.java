package com.ganhraufarm.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ganhraufarm.app.api.RetrofitClient;
import com.ganhraufarm.app.models.UserProfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";
    private TextView tvUserName, tvUserEmail, tvUserPhone, tvUserAddress;
    private String currentUserId;

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
        findViewById(R.id.cardOrderHistory).setOnClickListener(v -> {
            startActivity(new Intent(this, OrderHistoryActivity.class));
        });

        findViewById(R.id.btnEditPhone).setOnClickListener(v -> showEditDialog("phone", "Cập nhật số điện thoại", tvUserPhone.getText().toString()));
        findViewById(R.id.btnEditAddress).setOnClickListener(v -> showEditDialog("address", "Cập nhật địa chỉ", tvUserAddress.getText().toString()));
    }

    private void checkSession() {
        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        String token = prefs.getString("access_token", null);
        currentUserId = prefs.getString("user_id", null);

        if (token != null && currentUserId != null) {
            RetrofitClient.setAuthToken(token);
            fetchUserProfile(currentUserId);
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
        tvUserPhone.setText(profile.getPhone() != null && !profile.getPhone().isEmpty() ? profile.getPhone() : "Chưa cập nhật");
        tvUserAddress.setText(profile.getAddress() != null && !profile.getAddress().isEmpty() ? profile.getAddress() : "Chưa cập nhật");
    }

    private void showEditDialog(String field, String title, String currentValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);
        EditText etInput = view.findViewById(R.id.etInput);
        
        if (!currentValue.equals("Chưa cập nhật")) {
            etInput.setText(currentValue);
        }

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newValue = etInput.getText().toString().trim();
            if (!newValue.isEmpty()) {
                updateProfile(field, newValue);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void updateProfile(String field, String value) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(field, value);

        RetrofitClient.getApi().updateUserProfile("eq." + currentUserId, updates).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AccountActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    fetchUserProfile(currentUserId);
                } else {
                    Toast.makeText(AccountActivity.this, "Cập nhật thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Update failed", t);
                Toast.makeText(AccountActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
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
