package com.ganhraufarm.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ganhraufarm.app.adapter.CartAdapter;
import com.ganhraufarm.app.model.CartItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartUpdateListener {

    private RecyclerView rvCart;
    private CartAdapter adapter;
    private List<CartItem> cartList;
    private View layoutBottom, layoutEmpty;
    private TextView tvTotalPrice;
    private Button btnOrderNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        loadDummyData();
        setupRecyclerView();
        updateBottomBar();
    }

    private void initViews() {
        rvCart = findViewById(R.id.rvCart);
        layoutBottom = findViewById(R.id.layoutBottom);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnOrderNow = findViewById(R.id.btnOrderNow);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnShopNow).setOnClickListener(v -> finish());
        
        btnOrderNow.setOnClickListener(v -> {
            startActivity(new Intent(this, CheckoutActivity.class));
        });
    }

    private void loadDummyData() {
        cartList = new ArrayList<>();
        // Add a dummy item to match the screenshot
        cartList.add(new CartItem("1", "p1", "Xúc xích hong khói Đức Việt gói 450gr", 69900, 1, ""));
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter(cartList, this);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(adapter);
    }

    @Override
    public void onQuantityChanged(int position, int newQuantity) {
        cartList.get(position).setQuantity(newQuantity);
        adapter.notifyItemChanged(position);
        updateBottomBar();
    }

    @Override
    public void onSelectionChanged() {
        updateBottomBar();
    }

    @Override
    public void onDeleteItem(int position) {
        cartList.remove(position);
        adapter.notifyItemRemoved(position);
        updateBottomBar();
        checkEmptyState();
    }

    private void updateBottomBar() {
        long total = 0;
        int selectedCount = 0;
        for (CartItem item : cartList) {
            if (item.isSelected()) {
                total += item.getPrice() * item.getQuantity();
                selectedCount++;
            }
        }

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvTotalPrice.setText(formatter.format(total) + "đ");
        btnOrderNow.setText("Đặt mua (" + selectedCount + ")");
    }

    private void checkEmptyState() {
        if (cartList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvCart.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvCart.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);
        }
    }
}