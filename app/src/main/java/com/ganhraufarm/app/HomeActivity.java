package com.ganhraufarm.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ganhraufarm.app.databinding.ActivityHomeBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private ActivityHomeBinding binding;
    private final OkHttpClient httpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        String token = prefs.getString("access_token", null);
        String userId = prefs.getString("user_id", null);

        if (token != null && userId != null) {
            fetchUserProfile(token, userId);
        } else {
            handleLogout();
        }

        binding.btnLogout.setOnClickListener(v -> handleLogout());
    }

    private void fetchUserProfile(String token, String userId) {
        Log.d(TAG, "Fetching profile for user: " + userId);
        // Using "user" table as per previous context, prompt said "users" but "user" was used in DB
        // I will try "users" as requested by the prompt for Task 2.
        String url = SupabaseConfig.SUPABASE_URL + "/rest/v1/users?select=*&id=eq." + userId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SupabaseConfig.SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    try {
                        String responseData;
                        responseData = response.body().string();
                        Log.d(TAG, "Profile response: " + responseData);
                        JSONArray jsonArray = new JSONArray(responseData);
                        if (jsonArray.length() > 0) {
                            JSONObject profile = jsonArray.getJSONObject(0);
                            runOnUiThread(() -> displayData(profile));
                        } else {
                            // If "users" fails, maybe try "user"? Let's just stick to "users" for now.
                            runOnUiThread(() -> {
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(HomeActivity.this, "Không tìm thấy hồ sơ", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing profile", e);
                        runOnUiThread(() -> {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(HomeActivity.this, "Lỗi dữ liệu", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    Log.e(TAG, "Fetch profile failed: " + response.code());
                    runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(HomeActivity.this, "Lỗi tải dữ liệu: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void displayData(JSONObject obj) {
        binding.progressBar.setVisibility(View.GONE);
        binding.layoutContent.setVisibility(View.VISIBLE);
        
        binding.tvFullName.setText(obj.optString("full_name", "Chưa cập nhật"));
        binding.tvEmail.setText(obj.optString("email", "N/A"));
        binding.tvPhone.setText(obj.optString("phone", "Chưa cập nhật"));
        binding.tvAddress.setText(obj.optString("address", "Chưa cập nhật"));
        binding.tvRole.setText(obj.optString("role", "customer"));
    }

    private void handleLogout() {
        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
