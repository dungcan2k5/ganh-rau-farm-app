package com.ganhraufarm.app;

import android.content.SharedPreferences;
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

import com.ganhraufarm.app.api.RetrofitClient;
import com.ganhraufarm.app.model.CartItem;
import com.ganhraufarm.app.models.CheckoutRequest;
import com.ganhraufarm.app.models.CheckoutResponse;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    private static final String TAG = "CheckoutActivity";
    private LinearLayout layoutItems;
    private TextView tvBillSubtotal, tvBillTotal;
    private List<CartItem> checkoutList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initViews();
        loadSampleData();
        displayItems();
        calculateTotal();
    }

    private void initViews() {
        layoutItems = findViewById(R.id.layoutItems);
        tvBillSubtotal = findViewById(R.id.tvBillSubtotal);
        tvBillTotal = findViewById(R.id.tvBillTotal);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnPay).setOnClickListener(v -> performCheckout());
    }

    private void performCheckout() {
        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dummy data for demo
        String address = "62/250 Kim Giang, Phường Đại Kim, Quận Hoàng Mai, Hà Nội";
        String phone = "0333880117";
        String couponCode = ""; // No coupon for now

        CheckoutRequest request = new CheckoutRequest(userId, couponCode, address, phone);

        findViewById(R.id.btnPay).setEnabled(false);
        Toast.makeText(this, "Đang xử lý thanh toán...", Toast.LENGTH_SHORT).show();

        RetrofitClient.getApi().processPayment(request).enqueue(new Callback<CheckoutResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckoutResponse> call, @NonNull Response<CheckoutResponse> response) {
                findViewById(R.id.btnPay).setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    CheckoutResponse res = response.body();
                    if (res.isSuccess()) {
                        Toast.makeText(CheckoutActivity.this, "Đặt hàng thành công! ID: " + res.getOrderId(), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(CheckoutActivity.this, "Lỗi: " + res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Checkout failed: " + response.code());
                    Toast.makeText(CheckoutActivity.this, "Lỗi hệ thống", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CheckoutResponse> call, @NonNull Throwable t) {
                findViewById(R.id.btnPay).setEnabled(true);
                Log.e(TAG, "Network error", t);
                Toast.makeText(CheckoutActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSampleData() {
        checkoutList = new ArrayList<>();
        // Using sample data as seen in the mockup
        checkoutList.add(new CartItem("1", "p1", "Xúc xích hong khói Đức Việt gói 450gr", 69900, 1, ""));
    }

    private void displayItems() {
        layoutItems.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        for (CartItem item : checkoutList) {
            View itemView = inflater.inflate(R.layout.item_checkout_row, layoutItems, false);
            
            TextView tvName = itemView.findViewById(R.id.tvCheckoutItemName);
            TextView tvPrice = itemView.findViewById(R.id.tvCheckoutItemPrice);
            TextView tvQuantity = itemView.findViewById(R.id.tvCheckoutItemQuantity);
            
            tvName.setText(item.getProductName());
            tvPrice.setText(formatter.format(item.getPrice()) + "đ");
            tvQuantity.setText("x" + item.getQuantity());
            
            layoutItems.addView(itemView);
        }
    }

    private void calculateTotal() {
        long total = 0;
        for (CartItem item : checkoutList) {
            total += item.getPrice() * item.getQuantity();
        }

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String totalStr = formatter.format(total) + "đ";
        
        tvBillSubtotal.setText(totalStr);
        tvBillTotal.setText(totalStr);
    }
}