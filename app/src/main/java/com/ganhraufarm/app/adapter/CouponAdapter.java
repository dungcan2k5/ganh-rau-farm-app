package com.ganhraufarm.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ganhraufarm.app.R;
import com.ganhraufarm.app.models.Coupon;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {
    private List<Coupon> coupons;

    public CouponAdapter(List<Coupon> coupons) {
        this.coupons = coupons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Coupon coupon = coupons.get(position);
        
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String discountText = "";
        if ("percentage".equals(coupon.getDiscountType())) {
            discountText = "Giảm " + (int)coupon.getDiscountValue() + "%";
        } else {
            discountText = "Giảm " + formatter.format(coupon.getDiscountValue()) + "đ";
        }
        
        holder.tvDiscount.setText(discountText);
        holder.tvMinOrder.setText("Đơn trên " + formatter.format(coupon.getMinOrderValue()) + "đ");
        holder.tvStatus.setText("Mã: " + coupon.getCode());
    }

    @Override
    public int getItemCount() {
        return coupons != null ? coupons.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatus, tvDiscount, tvMinOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.textCoupon);
            tvDiscount = itemView.findViewById(R.id.textCoupon1);
            tvMinOrder = itemView.findViewById(R.id.textCoupon2);
        }
    }
}
