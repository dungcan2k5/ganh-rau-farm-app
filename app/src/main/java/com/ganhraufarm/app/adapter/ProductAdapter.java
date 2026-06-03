package com.ganhraufarm.app.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ganhraufarm.app.ProductDetailActivity;
import com.ganhraufarm.app.R;
import com.ganhraufarm.app.models.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products;
    private OnAddToCartClickListener onAddToCartClickListener;

    public interface OnAddToCartClickListener {
        void onAddToCartClick(Product product);
    }

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    public ProductAdapter(List<Product> products, OnAddToCartClickListener listener) {
        this.products = products;
        this.onAddToCartClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvName.setText(product.getName());
        
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(formatter.format(product.getPrice()) + "đ");

        Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            v.getContext().startActivity(intent);
        });

        holder.btnAdd.setOnClickListener(v -> {
            if (onAddToCartClickListener != null) {
                onAddToCartClickListener.onAddToCartClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, btnAdd;
        TextView tvName, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.imageProduct);
            tvName = itemView.findViewById(R.id.textProduct);
            tvPrice = itemView.findViewById(R.id.textProduct1);
            btnAdd = itemView.findViewById(R.id.imageProduct1);
        }
    }
}
