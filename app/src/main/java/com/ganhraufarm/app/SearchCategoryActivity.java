package com.ganhraufarm.app;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ganhraufarm.app.api.RetrofitClient;
import com.ganhraufarm.app.databinding.TimKiemPhanLoaiBinding;
import com.ganhraufarm.app.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchCategoryActivity extends AppCompatActivity {
    private TimKiemPhanLoaiBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TimKiemPhanLoaiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fetchAllProducts();
    }

    private void fetchAllProducts() {
        RetrofitClient.getApi().getProducts("*", "eq.true").enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update your RecyclerView adapter here
                    // adapter.submitList(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                Toast.makeText(SearchCategoryActivity.this, "Lỗi tải sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
