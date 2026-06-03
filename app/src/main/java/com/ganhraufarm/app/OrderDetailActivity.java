package com.ganhraufarm.app;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.ganhraufarm.app.api.RetrofitClient;
import com.ganhraufarm.app.databinding.ChiTietDonHangBinding;
import com.ganhraufarm.app.models.Order;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private ChiTietDonHangBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChiTietDonHangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId != -1) {
            fetchOrderDetails(orderId);
        }

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void fetchOrderDetails(int id) {
        // Fetch order with items and products joined
        RetrofitClient.getApi().getOrderDetails("eq." + id, "*,items(*,product:products(*))")
                .enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    displayOrder(response.body().get(0));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Lỗi tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayOrder(Order order) {
        // Update Address (Mocking for now as address is separate table usually)
        binding.tvRecipientNamePhone.setText("Người nhận: ID " + order.getUserId());
        binding.tvDetailedAddress.setText("Phương thức: " + order.getPaymentMethod());
        
        binding.tvTotalPrice.setText(String.format("%,.0fđ", order.getTotalAmount()));
        
        // Setup items RecyclerView if you have an adapter for it
        // binding.rvProducts.setLayoutManager(new LinearLayoutManager(this));
        // binding.rvProducts.setAdapter(new OrderItemAdapter(order.getItems()));
    }
}
