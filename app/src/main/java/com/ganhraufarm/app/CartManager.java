package com.ganhraufarm.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.ganhraufarm.app.model.CartItem;
import com.ganhraufarm.app.models.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final String PREF_NAME = "ganh_rau_cart_prefs";
    private static final String KEY_CART = "cart_items";
    private static CartManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;
    private List<CartItem> cartList;

    private CartManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadCart();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    private void loadCart() {
        String json = prefs.getString(KEY_CART, null);
        if (json != null) {
            Type type = new TypeToken<List<CartItem>>() {}.getType();
            cartList = gson.fromJson(json, type);
        } else {
            cartList = new ArrayList<>();
        }
    }

    public void saveCart() {
        String json = gson.toJson(cartList);
        prefs.edit().putString(KEY_CART, json).apply();
    }

    public List<CartItem> getCartList() {
        return cartList;
    }

    public void addItem(Product product) {
        // Check if item already exists
        for (CartItem item : cartList) {
            if (item.getProductId().equals(String.valueOf(product.getId()))) {
                item.setQuantity(item.getQuantity() + 1);
                saveCart();
                return;
            }
        }

        // Add new item
        CartItem newItem = new CartItem(
                String.valueOf(System.currentTimeMillis()), // Simple unique ID for cart item entry
                String.valueOf(product.getId()),
                product.getName(),
                (long) product.getPrice(),
                1,
                product.getImageUrl()
        );
        cartList.add(newItem);
        saveCart();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartList.size()) {
            cartList.remove(position);
            saveCart();
        }
    }

    public void updateQuantity(int position, int quantity) {
        if (position >= 0 && position < cartList.size()) {
            cartList.get(position).setQuantity(quantity);
            saveCart();
        }
    }

    public long getTotalPrice() {
        long total = 0;
        for (CartItem item : cartList) {
            if (item.isSelected()) {
                total += item.getPrice() * item.getQuantity();
            }
        }
        return total;
    }
    
    public int getSelectedCount() {
        int count = 0;
        for (CartItem item : cartList) {
            if (item.isSelected()) {
                count++;
            }
        }
        return count;
    }

    public List<CartItem> getSelectedItems() {
        List<CartItem> selectedItems = new ArrayList<>();
        for (CartItem item : cartList) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    public void removeSelectedItems() {
        List<CartItem> remainingItems = new ArrayList<>();
        for (CartItem item : cartList) {
            if (!item.isSelected()) {
                remainingItems.add(item);
            }
        }
        cartList = remainingItems;
        saveCart();
    }

    public void clearCart() {
        cartList.clear();
        saveCart();
    }
}
