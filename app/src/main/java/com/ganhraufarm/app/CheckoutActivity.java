package com.ganhraufarm.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ganhraufarm.app.api.RetrofitClient;
import com.ganhraufarm.app.model.CartItem;
import com.ganhraufarm.app.models.CheckoutRequest;
import com.ganhraufarm.app.models.CheckoutResponse;
import com.ganhraufarm.app.models.Coupon;
import com.ganhraufarm.app.models.SyncCartRequest;
import com.ganhraufarm.app.models.UserProfile;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    private static final String TAG = "CheckoutActivity";
    private LinearLayout layoutItems;
    private TextView tvBillSubtotal, tvBillTotal, tvDiscount;
    private EditText etReceiverName, etReceiverPhone, etAddress;
    private Spinner spCoupons;
    private View layoutDiscount;
    private List<CartItem> checkoutList;
    private List<Coupon> couponList = new ArrayList<>();
    private String selectedCouponCode = "";
    private long totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initViews();
        loadCartData();
        fetchUserProfile();
        displayItems();
        calculateTotal();
        fetchCoupons();
    }

    private void initViews() {
        layoutItems = findViewById(R.id.layoutItems);
        tvBillSubtotal = findViewById(R.id.tvBillSubtotal);
        tvBillTotal = findViewById(R.id.tvBillTotal);
        tvDiscount = findViewById(R.id.tvDiscount);
        layoutDiscount = findViewById(R.id.layoutDiscount);
        etReceiverName = findViewById(R.id.etReceiverName);
        etReceiverPhone = findViewById(R.id.etReceiverPhone);
        etAddress = findViewById(R.id.etAddress);
        spCoupons = findViewById(R.id.spCoupons);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnPay).setOnClickListener(v -> startSyncAndPay());
    }

    private void loadCartData() {
        checkoutList = CartManager.getInstance(this).getSelectedItems();
        if (checkoutList.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm nào được chọn", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchUserProfile() {
        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) return;

        RetrofitClient.getApi().getUserProfile("eq." + userId, "*").enqueue(new Callback<List<UserProfile>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserProfile>> call, @NonNull Response<List<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    UserProfile profile = response.body().get(0);
                    etReceiverName.setText(profile.getFullName());
                    etReceiverPhone.setText(profile.getPhone());
                    etAddress.setText(profile.getAddress());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserProfile>> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to fetch user profile", t);
            }
        });
    }

    private void fetchCoupons() {
        RetrofitClient.getApi().getCoupons("*", "eq.true").enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(@NonNull Call<List<Coupon>> call, @NonNull Response<List<Coupon>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    couponList.clear();
                    couponList.add(null); 
                    couponList.addAll(response.body());
                    setupCouponSpinner();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Coupon>> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to fetch coupons", t);
            }
        });
    }

    private void setupCouponSpinner() {
        ArrayAdapter<Coupon> adapter = new ArrayAdapter<Coupon>(this, android.R.layout.simple_spinner_item, couponList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv;
                if (convertView == null) {
                    tv = (TextView) LayoutInflater.from(CheckoutActivity.this)
                            .inflate(android.R.layout.simple_spinner_item, parent, false);
                } else {
                    tv = (TextView) convertView;
                }

                Coupon coupon = getItem(position);
                if (coupon == null) {
                    tv.setText("Chọn mã giảm giá");
                } else {
                    tv.setText(coupon.getCode());
                    if (totalAmount < coupon.getMinOrderValue()) {
                        tv.setTextColor(android.graphics.Color.GRAY);
                    } else {
                        tv.setTextColor(android.graphics.Color.BLACK);
                    }
                }
                return tv;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv;
                if (convertView == null) {
                    tv = (TextView) LayoutInflater.from(CheckoutActivity.this)
                            .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                } else {
                    tv = (TextView) convertView;
                }

                Coupon coupon = getItem(position);
                if (coupon == null) {
                    tv.setText("Không sử dụng");
                } else {
                    NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                    String detail = coupon.getCode() + " (Giảm " + formatter.format(coupon.getDiscountValue()) + 
                                    (coupon.getDiscountType().equals("percentage") ? "%" : "đ") + 
                                    " - Tối thiểu " + formatter.format(coupon.getMinOrderValue()) + "đ)";
                    tv.setText(detail);
                    
                    if (totalAmount < coupon.getMinOrderValue()) {
                        tv.setTextColor(android.graphics.Color.GRAY);
                    } else {
                        tv.setTextColor(android.graphics.Color.BLACK);
                    }
                }
                return tv;
            }

            @Override
            public boolean isEnabled(int position) {
                Coupon coupon = getItem(position);
                if (coupon == null) return true;
                return totalAmount >= coupon.getMinOrderValue();
            }
        };
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCoupons.setAdapter(adapter);
        
        spCoupons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Coupon coupon = couponList.get(position);
                if (coupon != null && totalAmount >= coupon.getMinOrderValue()) {
                    selectedCouponCode = coupon.getCode();
                    applyCouponEffects(coupon);
                } else {
                    selectedCouponCode = "";
                    removeCouponEffects();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCouponCode = "";
                removeCouponEffects();
            }
        });
    }

    private void applyCouponEffects(Coupon coupon) {
        long discount = 0;
        if (coupon.getDiscountType().equals("percentage")) {
            discount = (long) (totalAmount * (coupon.getDiscountValue() / 100.0));
        } else {
            discount = (long) coupon.getDiscountValue();
        }

        layoutDiscount.setVisibility(View.VISIBLE);
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvDiscount.setText("-" + formatter.format(discount) + "đ");
        tvBillTotal.setText(formatter.format(totalAmount - discount) + "đ");
    }

    private void removeCouponEffects() {
        layoutDiscount.setVisibility(View.GONE);
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvBillTotal.setText(formatter.format(totalAmount) + "đ");
    }

    private void startSyncAndPay() {
        String name = etReceiverName.getText().toString().trim();
        String phone = etReceiverPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin nhận hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("ganh_rau_prefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        findViewById(R.id.btnPay).setEnabled(false);
        Toast.makeText(this, "Đang chuẩn bị đơn hàng...", Toast.LENGTH_SHORT).show();

        // 1. Clear existing cart in DB
        RetrofitClient.getApi().clearCartItems("eq." + userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                // Status 204 or 200 is success for DELETE
                if (response.isSuccessful() || response.code() == 204) {
                    // 2. Sync local selected items to DB
                    syncSelectedItems(userId, address, phone);
                } else {
                    findViewById(R.id.btnPay).setEnabled(true);
                    Toast.makeText(CheckoutActivity.this, "Lỗi chuẩn bị giỏ hàng (1)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                findViewById(R.id.btnPay).setEnabled(true);
                Toast.makeText(CheckoutActivity.this, "Lỗi kết nối (1)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void syncSelectedItems(String userId, String address, String phone) {
        List<SyncCartRequest.CartItemBody> syncItems = new ArrayList<>();
        for (CartItem item : checkoutList) {
            syncItems.add(new SyncCartRequest.CartItemBody(
                    userId,
                    Integer.parseInt(item.getProductId()),
                    item.getQuantity()
            ));
        }

        RetrofitClient.getApi().syncCartItems(syncItems).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful() || response.code() == 201) {
                    // 3. Proceed to payment
                    performPayment(userId, address, phone);
                } else {
                    findViewById(R.id.btnPay).setEnabled(true);
                    Toast.makeText(CheckoutActivity.this, "Lỗi chuẩn bị giỏ hàng (2)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                findViewById(R.id.btnPay).setEnabled(true);
                Toast.makeText(CheckoutActivity.this, "Lỗi kết nối (2)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performPayment(String userId, String address, String phone) {
        List<CheckoutRequest.CheckoutItem> checkoutItems = new ArrayList<>();
        for (CartItem item : checkoutList) {
            checkoutItems.add(new CheckoutRequest.CheckoutItem(
                    Integer.parseInt(item.getProductId()),
                    item.getQuantity()
            ));
        }

        CheckoutRequest request = new CheckoutRequest(userId, selectedCouponCode, address, phone, checkoutItems);

        RetrofitClient.getApi().processPayment(request).enqueue(new Callback<CheckoutResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckoutResponse> call, @NonNull Response<CheckoutResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CheckoutResponse res = response.body();
                    if (res.isSuccess()) {
                        // 4. Update coupon usage if applicable
                        if (selectedCouponCode != null && !selectedCouponCode.isEmpty()) {
                            decrementCouponUsage(selectedCouponCode);
                        } else {
                            completeCheckout();
                        }
                    } else {
                        findViewById(R.id.btnPay).setEnabled(true);
                        Toast.makeText(CheckoutActivity.this, "Lỗi: " + res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    findViewById(R.id.btnPay).setEnabled(true);
                    Log.e(TAG, "Checkout failed: " + response.code());
                    Toast.makeText(CheckoutActivity.this, "Lỗi hệ thống (3)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CheckoutResponse> call, @NonNull Throwable t) {
                findViewById(R.id.btnPay).setEnabled(true);
                Log.e(TAG, "Network error", t);
                Toast.makeText(CheckoutActivity.this, "Lỗi kết nối (3)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void decrementCouponUsage(String couponCode) {
        // Find current coupon to get usage_limit
        Coupon selectedCoupon = null;
        for (Coupon c : couponList) {
            if (c != null && c.getCode().equals(couponCode)) {
                selectedCoupon = c;
                break;
            }
        }

        if (selectedCoupon == null) {
            completeCheckout();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        // Note: This logic assumes 'usage_limit' in table is the quantity available.
        // We decrement it by 1.
        updates.put("usage_limit", Math.max(0, selectedCoupon.getUsageLimit() - 1));

        RetrofitClient.getApi().updateCoupon("eq." + couponCode, updates).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                completeCheckout();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to update coupon usage", t);
                completeCheckout(); // Still complete even if coupon update fails
            }
        });
    }

    private void completeCheckout() {
        findViewById(R.id.btnPay).setEnabled(true);
        Toast.makeText(CheckoutActivity.this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
        CartManager.getInstance(CheckoutActivity.this).removeSelectedItems();
        finish();
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
            ImageView ivProduct = itemView.findViewById(R.id.ivCheckoutItem);
            
            tvName.setText(item.getProductName());
            tvPrice.setText(formatter.format(item.getPrice()) + "đ");
            tvQuantity.setText("x" + item.getQuantity());

            com.bumptech.glide.Glide.with(this)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(ivProduct);
            
            layoutItems.addView(itemView);
        }
    }

    private void calculateTotal() {
        totalAmount = 0;
        for (CartItem item : checkoutList) {
            totalAmount += item.getPrice() * item.getQuantity();
        }

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String totalStr = formatter.format(totalAmount) + "đ";
        
        tvBillSubtotal.setText(totalStr);
        tvBillTotal.setText(totalStr);
    }
}
