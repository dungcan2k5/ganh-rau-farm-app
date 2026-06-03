package com.ganhraufarm.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ganhraufarm.app.adapters.OrderAdapter;
import com.ganhraufarm.app.api.RetrofitClient;
import com.ganhraufarm.app.databinding.LichSuMuaHangBinding;
import com.ganhraufarm.app.models.Order;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity {
    private LichSuMuaHangBinding binding;
    private OrderAdapter adapter;
    private List<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LichSuMuaHangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();

        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId != null) {
            fetchOrderHistory(userId);
        }

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new OrderAdapter(orders, order -> {
            Intent intent = new Intent(OrderHistoryActivity.this, OrderDetailActivity.class);
            intent.putExtra("ORDER_ID", order.getId());
            startActivity(intent);
        });
        binding.rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrderHistory.setAdapter(adapter);
    }

    private void fetchOrderHistory(String userId) {
        // Query orders with items and products
        RetrofitClient.getApi().getOrders("eq." + userId, "*,items(*,product:products(*))")
                .enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orders.clear();
                    orders.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                Toast.makeText(OrderHistoryActivity.this, "Lỗi tải lịch sử", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
