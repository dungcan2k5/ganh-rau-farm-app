package com.ganhraufarm.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ganhraufarm.app.model.CartItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

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
        findViewById(R.id.btnPay).setOnClickListener(v -> {
            Toast.makeText(this, "Đang xử lý thanh toán...", Toast.LENGTH_SHORT).show();
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