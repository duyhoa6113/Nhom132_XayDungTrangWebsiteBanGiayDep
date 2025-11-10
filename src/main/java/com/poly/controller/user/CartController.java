package com.poly.controller.user;

import com.poly.dto.CartItemDTO;
import com.poly.entity.CartItem;
import com.poly.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public Collection<CartItem> viewCart() {
        return cartService.getItems();
    }

    @PostMapping("/add")
    public String addItem(@RequestBody CartItemDTO dto) {
        CartItem item = new CartItem(dto.getProductId(), dto.getProductName(), dto.getPrice(), dto.getQuantity());
        cartService.addItem(item);
        return "Đã thêm vào giỏ hàng";
    }

    @PutMapping("/update/{id}")
    public String updateQuantity(@PathVariable("id") Long id, @RequestParam("quantity") int quantity) {
        cartService.updateQuantity(id, quantity);
        return "Đã cập nhật số lượng";
    }

    @DeleteMapping("/remove/{id}")
    public String removeItem(@PathVariable("id") Long id) {
        cartService.removeItem(id);
        return "Đã xóa sản phẩm";
    }

    @PostMapping("/coupon")
    public Map<String, Object> applyCoupon(@RequestParam("code") String code) {
        boolean success = cartService.applyCoupon(code);
        return Map.of(
                "success", success,
                "total", cartService.getTotal()
        );
    }

    @GetMapping("/total")
    public double getTotal() {
        return cartService.getTotal();
    }

    @DeleteMapping("/clear")
    public String clearCart() {
        cartService.clear();
        return "Đã xóa giỏ hàng";
    }
}
