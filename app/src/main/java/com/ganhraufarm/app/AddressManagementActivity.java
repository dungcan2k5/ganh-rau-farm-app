package com.ganhraufarm.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ganhraufarm.app.adapters.AddressAdapter;
import com.ganhraufarm.app.api.RetrofitClient;
import com.ganhraufarm.app.databinding.QuanLyDiaChiBinding;
import com.ganhraufarm.app.models.Address;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressManagementActivity extends AppCompatActivity {
    private QuanLyDiaChiBinding binding;
    private AddressAdapter adapter;
    private final List<Address> addressList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = QuanLyDiaChiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();

        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId != null) {
            fetchAddresses(userId);
        }

        binding.toolbarAddress.setNavigationOnClickListener(v -> finish());
        binding.btnAddAddress.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng thêm địa chỉ đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerView() {
        adapter = new AddressAdapter(addressList, address -> {
            Toast.makeText(this, "Chỉnh sửa: " + address.getRecipientName(), Toast.LENGTH_SHORT).show();
        });
        binding.rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAddresses.setAdapter(adapter);
    }

    private void fetchAddresses(String userId) {
        binding.progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getApi().getAddresses("eq." + userId, "*").enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(@NonNull Call<List<Address>> call, @NonNull Response<List<Address>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    addressList.clear();
                    addressList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Address>> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(AddressManagementActivity.this, "Lỗi tải địa chỉ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
