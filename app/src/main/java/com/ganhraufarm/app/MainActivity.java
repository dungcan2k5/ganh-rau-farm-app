package com.ganhraufarm.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity đóng vai trò là một Router (Bộ điều hướng).
 * Nhiệm vụ duy nhất là kiểm tra trạng thái đăng nhập từ SharedPreferences
 * và chuyển hướng người dùng đến màn hình tương ứng.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Router Activity started");

        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        String token = prefs.getString("access_token", null);

        if (token != null) {
            Log.d(TAG, "User session found, redirecting to HomeActivity");
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            Log.d(TAG, "No user session, redirecting to LoginActivity");
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Đóng MainActivity để người dùng không thể quay lại bằng nút Back
        finish();
    }
}
