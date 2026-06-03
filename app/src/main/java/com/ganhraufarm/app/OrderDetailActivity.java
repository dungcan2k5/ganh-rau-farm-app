package com.ganhraufarm.app;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ganhraufarm.app.api.RetrofitClient;
import com.ganhraufarm.app.models.Order;
import com.ganhraufarm.app.models.OrderItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private static final String TAG = "OrderDetailActivity";
    private TextView tvOrderDate, tvOrderId, tvReceiverName, tvReceiverPhone, tvAddress;
    private TextView tvPaymentMethod, tvBillSubtotal, tvBillTotal, tvDiscount, tvOrderStatus;
    private LinearLayout layoutItems;
    private View layoutDiscount;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chi_tiet_don_hang);

        orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        fetchOrderDetails();
    }

    private void initViews() {
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvReceiverName = findViewById(R.id.tvReceiverName);
        tvReceiverPhone = findViewById(R.id.tvReceiverPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvBillSubtotal = findViewById(R.id.tvBillSubtotal);
        tvBillTotal = findViewById(R.id.tvBillTotal);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        layoutItems = findViewById(R.id.layoutItems);
        layoutDiscount = findViewById(R.id.layoutDiscount);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void fetchOrderDetails() {
        // Query order with items and products
        RetrofitClient.getApi().getOrderDetails("eq." + orderId, "*,items:order_items(*,product:products(*))")
                .enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    displayOrderData(response.body().get(0));
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Không thể tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                Log.e(TAG, "Fetch failed", t);
                Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayOrderData(Order order) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        tvOrderId.setText("Mã đơn hàng: #" + order.getId());
        tvOrderDate.setText("Ngày đặt: " + order.getCreatedAt()); 
        
        // Receiver Info
        tvAddress.setText("Địa chỉ: " + (order.getShippingAddress() != null ? order.getShippingAddress() : "N/A"));
        tvReceiverPhone.setText("Số điện thoại: " + (order.getReceiverPhone() != null ? order.getReceiverPhone() : "N/A"));
        
        // Payment Method
        tvPaymentMethod.setText(order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A");

        // Totals
        tvBillSubtotal.setText(formatter.format(order.getTotalAmount()) + "đ");
        tvBillTotal.setText(formatter.format(order.getFinalAmount()) + "đ");

        long discount = (long) (order.getTotalAmount() - order.getFinalAmount());
        if (discount > 0) {
            layoutDiscount.setVisibility(View.VISIBLE);
            tvDiscount.setText("-" + formatter.format(discount) + "đ");
        } else {
            layoutDiscount.setVisibility(View.GONE);
        }

        // Status
        setStatusUI(order.getStatus());

        // Items
        displayItems(order.getItems());
    }

    private void setStatusUI(String status) {
        if (status == null) status = "";
        
        switch (status.toLowerCase()) {
            case "pending":
                tvOrderStatus.setText("Chờ xử lý");
                tvOrderStatus.setTextColor(Color.parseColor("#F57C00"));
                break;
            case "processing":
                tvOrderStatus.setText("Đang xử lý");
                tvOrderStatus.setTextColor(Color.parseColor("#1976D2"));
                break;
            case "shipped":
                tvOrderStatus.setText("Đang giao hàng");
                tvOrderStatus.setTextColor(Color.parseColor("#00796B"));
                break;
            case "delivered":
                tvOrderStatus.setText("Đã giao hàng");
                tvOrderStatus.setTextColor(Color.parseColor("#303F9F"));
                break;
            case "completed":
                tvOrderStatus.setText("Hoàn thành");
                tvOrderStatus.setTextColor(Color.parseColor("#388E3C"));
                break;
            case "cancelled":
                tvOrderStatus.setText("Đã hủy");
                tvOrderStatus.setTextColor(Color.parseColor("#D32F2F"));
                break;
            default:
                tvOrderStatus.setText(status);
                tvOrderStatus.setTextColor(Color.BLACK);
                break;
        }
    }

    private void displayItems(List<OrderItem> items) {
        layoutItems.removeAllViews();
        if (items == null) return;

        LayoutInflater inflater = LayoutInflater.from(this);
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        for (OrderItem item : items) {
            View itemView = inflater.inflate(R.layout.item_checkout_row, layoutItems, false);
            
            TextView tvName = itemView.findViewById(R.id.tvCheckoutItemName);
            TextView tvPrice = itemView.findViewById(R.id.tvCheckoutItemPrice);
            TextView tvQuantity = itemView.findViewById(R.id.tvCheckoutItemQuantity);
            ImageView ivProduct = itemView.findViewById(R.id.ivCheckoutItem);
            
            if (item.getProduct() != null) {
                tvName.setText(item.getProduct().getName());
                Glide.with(this)
                        .load(item.getProduct().getImageUrl())
                        .placeholder(R.drawable.ic_nav_cart)
                        .into(ivProduct);
            } else {
                tvName.setText("Sản phẩm #" + item.getProductId());
            }
            
            tvPrice.setText(formatter.format(item.getPrice()) + "đ");
            tvQuantity.setText("x" + item.getQuantity());
            
            layoutItems.addView(itemView);
        }
    }
}
