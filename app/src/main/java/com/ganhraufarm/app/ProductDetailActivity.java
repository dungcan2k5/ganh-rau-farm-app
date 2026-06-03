package com.ganhraufarm.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ganhraufarm.app.api.RetrofitClient;
import com.ganhraufarm.app.databinding.ChiTietSanPhamBinding;
import com.ganhraufarm.app.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    private ChiTietSanPhamBinding binding;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChiTietSanPhamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId != -1) {
            fetchProductDetails(productId);
        } else {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void fetchProductDetails(int id) {
        // Sử dụng select=*,categories(name) để lấy thông tin category từ bảng categories
        RetrofitClient.getApi().getProductById("eq." + id, "*,categories(name)").enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    displayProduct(response.body().get(0));
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Lỗi tải chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProduct(Product product) {
        binding.tvDetailName.setText(product.getName());
        binding.tvDetailCategory.setText(product.getCategory() != null ? product.getCategory().toUpperCase() : "SẢN PHẨM");
        binding.tvDetailPrice.setText(String.format("%,.0fđ", product.getPrice()));

        // Hiển thị số lượng tồn kho
        binding.tvDetailStock.setText("Kho: " + product.getStock());

        binding.tvDetailDescription.setText(product.getDescription());

        Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.ivProductLarge);

        // Click listeners cho các nút chức năng
        binding.btnAddToCartDetail.setOnClickListener(v -> {
            CartManager.getInstance(this).addItem(product);
            Toast.makeText(this, "Đã thêm " + product.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });

        binding.btnBuyNow.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng mua ngay đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }
}
