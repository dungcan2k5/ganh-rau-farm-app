package com.ganhraufarm.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ganhraufarm.app.R;
import com.ganhraufarm.app.models.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private CartUpdateListener listener;

    public interface CartUpdateListener {
        void onQuantityChanged(int position, int newQuantity);
        void onSelectionChanged();
        void onDeleteItem(int position);
    }

    public CartAdapter(List<CartItem> cartItems, CartUpdateListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.tvName.setText(item.getProductName());
        
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(formatter.format(item.getPrice()) + "đ");
        
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.cbSelect.setChecked(item.isSelected());

        holder.btnPlus.setOnClickListener(v -> listener.onQuantityChanged(position, item.getQuantity() + 1));
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                listener.onQuantityChanged(position, item.getQuantity() - 1);
            }
        });
        
        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            listener.onSelectionChanged();
        });

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteItem(position));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity;
        ImageView ivProduct, btnDelete;
        ImageButton btnPlus, btnMinus;
        CheckBox cbSelect;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            cbSelect = itemView.findViewById(R.id.cbSelect);
        }
    }
}