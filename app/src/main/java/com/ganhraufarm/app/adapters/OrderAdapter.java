package com.ganhraufarm.app.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ganhraufarm.app.R;
import com.ganhraufarm.app.models.Order;
import com.ganhraufarm.app.models.OrderItem;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Order> orders;
    private final OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderAdapter(List<Order> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        
        holder.tvShopName.setText("Gánh Rau Farm");
        
        String status = order.getStatus();
        if (status == null) status = "";
        
        switch (status.toLowerCase()) {
            case "pending":
                holder.tvOrderStatus.setText("Chờ xử lý");
                holder.tvOrderStatus.setTextColor(Color.parseColor("#F57C00"));
                break;
            case "processing":
                holder.tvOrderStatus.setText("Đang xử lý");
                holder.tvOrderStatus.setTextColor(Color.parseColor("#1976D2"));
                break;
            case "shipped":
                holder.tvOrderStatus.setText("Đang giao hàng");
                holder.tvOrderStatus.setTextColor(Color.parseColor("#00796B"));
                break;
            case "delivered":
                holder.tvOrderStatus.setText("Đã giao hàng");
                holder.tvOrderStatus.setTextColor(Color.parseColor("#303F9F"));
                break;
            case "completed":
                holder.tvOrderStatus.setText("Hoàn thành");
                holder.tvOrderStatus.setTextColor(Color.parseColor("#388E3C"));
                break;
            case "cancelled":
                holder.tvOrderStatus.setText("Đã hủy");
                holder.tvOrderStatus.setTextColor(Color.parseColor("#D32F2F"));
                break;
            default:
                holder.tvOrderStatus.setText(status);
                holder.tvOrderStatus.setTextColor(Color.BLACK);
                break;
        }
        
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvOrderTotal.setText(formatter.format(order.getTotalAmount()) + "đ");

        // Format and display Order Date
        if (order.getCreatedAt() != null) {
            try {
                // Assuming Supabase timestamp format: 2024-06-04T01:23:54.123456+00:00
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = inputFormat.parse(order.getCreatedAt());
                
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                holder.tvOrderDate.setText(outputFormat.format(date));
            } catch (Exception e) {
                holder.tvOrderDate.setText(order.getCreatedAt());
            }
        }

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            OrderItem firstItem = order.getItems().get(0);
            if (firstItem.getProduct() != null) {
                holder.tvProductName.setText(firstItem.getProduct().getName());
                
                double displayPrice = firstItem.getPrice();
                holder.tvProductPrice.setText(formatter.format(displayPrice) + "đ");
                holder.tvProductQuantity.setText("x" + firstItem.getQuantity());
                
                Glide.with(holder.itemView.getContext())
                        .load(firstItem.getProduct().getImageUrl())
                        .placeholder(R.drawable.ic_nav_cart)
                        .error(R.drawable.ic_nav_cart)
                        .into(holder.ivProduct);
            }
        }

        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvShopName, tvOrderStatus, tvProductName, tvProductQuantity, tvProductPrice, tvOrderTotal, tvOrderDate;
        ImageView ivProduct;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShopName = itemView.findViewById(R.id.tvShopName);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            ivProduct = itemView.findViewById(R.id.ivProduct);
        }
    }
}
