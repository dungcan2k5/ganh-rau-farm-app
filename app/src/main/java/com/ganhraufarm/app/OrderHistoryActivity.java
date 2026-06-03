package com.ganhraufarm.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ganhraufarm.app.adapters.OrderAdapter;
import com.ganhraufarm.app.api.RetrofitClient;
import com.ganhraufarm.app.databinding.LichSuMuaHangBinding;
import com.ganhraufarm.app.models.Order;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity {
    private static final String TAG = "OrderHistoryActivity";
    private LichSuMuaHangBinding binding;
    private OrderAdapter adapter;
    private List<Order> allOrders = new ArrayList<>();
    private List<Order> filteredOrders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LichSuMuaHangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupTabs();

        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId != null) {
            fetchOrderHistory(userId);
        }

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new OrderAdapter(filteredOrders, order -> {
            Intent intent = new Intent(OrderHistoryActivity.this, OrderDetailActivity.class);
            intent.putExtra("ORDER_ID", order.getId());
            startActivity(intent);
        });
        binding.rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrderHistory.setAdapter(adapter);
    }

    private void setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterOrders(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void fetchOrderHistory(String userId) {
        Log.d(TAG, "Fetching order history for userId: " + userId);
        
        // Query orders with items and products
        // Mapping 'items' alias to 'order_items' table for Supabase/PostgREST join
        RetrofitClient.getApi().getOrders("eq." + userId, "*,items:order_items(*,product:products(*))")
                .enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                Log.d(TAG, "API URL: " + call.request().url());
                Log.d(TAG, "Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<Order> fetchedOrders = response.body();
                    Log.d(TAG, "Fetched " + fetchedOrders.size() + " orders");
                    
                    for (Order order : fetchedOrders) {
                        Log.d(TAG, "Order #" + order.getId() + " - Status: " + order.getStatus());
                        if (order.getItems() != null) {
                            Log.d(TAG, "  Items: " + order.getItems().size());
                        }
                    }

                    allOrders.clear();
                    allOrders.addAll(fetchedOrders);
                    filterOrders(binding.tabLayout.getSelectedTabPosition());
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "API Error: " + error);
                    } catch (Exception e) {
                        Log.e(TAG, "Error logging failure", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                Log.e(TAG, "Network Failure", t);
                Toast.makeText(OrderHistoryActivity.this, "Lỗi tải lịch sử", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterOrders(int tabPosition) {
        filteredOrders.clear();
        String statusFilter;
        switch (tabPosition) {
            case 1: statusFilter = "pending"; break;
            case 2: statusFilter = "processing"; break;
            case 3: statusFilter = "shipped"; break;
            case 4: statusFilter = "delivered"; break;
            case 5: statusFilter = "completed"; break;
            case 6: statusFilter = "cancelled"; break;
            default: statusFilter = "all"; break;
        }
        
        Log.d(TAG, "Filtering by status: " + statusFilter);

        if (statusFilter.equals("all")) {
            filteredOrders.addAll(allOrders);
        } else {
            for (Order order : allOrders) {
                if (order.getStatus() != null && order.getStatus().equalsIgnoreCase(statusFilter)) {
                    filteredOrders.add(order);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
