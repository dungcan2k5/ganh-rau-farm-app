package com.ganhraufarm.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
        RetrofitClient.getApi().getProductById("eq." + id, "*").enqueue(new Callback<List<Product>>() {
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
        binding.tvDetailCategory.setText(product.getCategory());
        binding.tvShopName.setText("Shop: " + product.getShopName());
        binding.tvRating.setText(String.valueOf(product.getRating()));
//        binding.tvRatingCount.setText("(" + product.getRatingCount() + " đánh giá)");
        binding.tvDetailPrice.setText(String.format("%,.0fđ", product.getPrice()));
        
        if (product.getOldPrice() != null) {
            binding.tvDetailOldPrice.setVisibility(View.VISIBLE);
            binding.tvDetailOldPrice.setText(String.format("%,.0fđ", product.getOldPrice()));
        } else {
            binding.tvDetailOldPrice.setVisibility(View.GONE);
        }

        binding.tvDetailDescription.setText(product.getDescription());
        
        // Note: For actual image loading, you would use Glide or Picasso
        // Glide.with(this).load(product.getImageUrl()).into(binding.ivProductLarge);
    }
}
