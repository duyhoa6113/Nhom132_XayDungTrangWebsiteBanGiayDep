package com.poly.service;

import com.poly.entity.CartItem;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartService {
    private Map<Long, CartItem> cartItems = new LinkedHashMap<>();
    private double shippingFee = 15000;
    private double discountRate = 0;

    // Thêm sản phẩm
    public void addItem(CartItem item) {
        if (cartItems.containsKey(item.getProductId())) {
            CartItem existing = cartItems.get(item.getProductId());
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
        } else {
            cartItems.put(item.getProductId(), item);
        }
    }

    // Xóa sản phẩm
    public void removeItem(Long id) {
        cartItems.remove(id);
    }

    // Cập nhật số lượng
    public void updateQuantity(Long id, int quantity) {
        if (cartItems.containsKey(id)) {
            CartItem item = cartItems.get(id);
            item.setQuantity(quantity);
        }
    }

    // Áp mã giảm giá
    public boolean applyCoupon(String code) {
        switch (code.toUpperCase()) {
            case "GIAM10":
                discountRate = 0.1;
                return true;
            case "SALE20":
                discountRate = 0.2;
                return true;
            default:
                discountRate = 0;
                return false;
        }
    }

    // Tính tổng tiền
    public double getTotal() {
        double subtotal = cartItems.values().stream().mapToDouble(CartItem::getTotal).sum();
        double discount = subtotal * discountRate;
        return subtotal - discount + shippingFee;
    }

    public Collection<CartItem> getItems() {
        return cartItems.values();
    }

    public void clear() {
        cartItems.clear();
        discountRate = 0;
    }
}
