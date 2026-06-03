package com.ganhraufarm.app.adapters;

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
import java.util.List;
import java.util.Locale;

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
        
        holder.tvShopName.setText("Gánh Rau Farm"); // Hardcoded as per design or fetch from order if available
        holder.tvOrderStatus.setText(order.getStatus());
        
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvOrderTotal.setText(formatter.format(order.getTotalAmount()) + "đ");

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            OrderItem firstItem = order.getItems().get(0);
            if (firstItem.getProduct() != null) {
                holder.tvProductName.setText(firstItem.getProduct().getName());
                holder.tvProductPrice.setText(formatter.format(firstItem.getPrice()) + "đ");
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
        TextView tvShopName, tvOrderStatus, tvProductName, tvProductQuantity, tvProductPrice, tvOrderTotal;
        ImageView ivProduct;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShopName = itemView.findViewById(R.id.tvShopName);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            ivProduct = itemView.findViewById(R.id.ivProduct);
        }
    }
}
