package com.ganhraufarm.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";
    private TextView tvUserName, tvUserEmail, tvUserPhone, tvUserAddress;
    private final OkHttpClient httpClient = new OkHttpClient();

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
            fetchUserProfile(token, userId);
        } else {
            handleLogout();
        }
    }

    private void fetchUserProfile(String token, String userId) {
        // Querying "users" table in Supabase
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
                runOnUiThread(() -> Toast.makeText(AccountActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);
                        if (jsonArray.length() > 0) {
                            JSONObject profile = jsonArray.getJSONObject(0);
                            runOnUiThread(() -> displayData(profile));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing profile", e);
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(AccountActivity.this, "Không thể tải hồ sơ", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void displayData(JSONObject obj) {
        tvUserName.setText(obj.optString("full_name", "N/A"));
        tvUserEmail.setText(obj.optString("email", "N/A"));
        tvUserPhone.setText(obj.optString("phone", "Chưa cập nhật"));
        tvUserAddress.setText(obj.optString("address", "Chưa cập nhật"));
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