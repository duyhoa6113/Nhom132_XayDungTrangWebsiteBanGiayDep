package com.poly.controller.user;

import com.poly.dto.AddToCartRequest;
import com.poly.dto.CartItemDTO;
import com.poly.dto.UpdateCartRequest;
import com.poly.entity.KhachHang;
import com.poly.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    /**
     * Hiển thị trang giỏ hàng
     */
    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

        if (khachHang == null) {
            // Nếu chưa đăng nhập, hiển thị giỏ hàng trống
            model.addAttribute("cartItems", List.of());
            model.addAttribute("cartCount", 0);
            return "user/cart";
        }

        try {
            List<CartItemDTO> cartItems = cartService.getCartItems(khachHang);
            Integer cartCount = cartService.getCartCount(khachHang);

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("cartCount", cartCount);

            log.info("Loaded cart with {} items for customer: {}",
                    cartItems.size(), khachHang.getKhachHangId());

        } catch (Exception e) {
            log.error("Error loading cart", e);
            model.addAttribute("cartItems", List.of());
            model.addAttribute("cartCount", 0);
            model.addAttribute("error", "Có lỗi khi tải giỏ hàng");
        }

        return "user/cart";
    }

    /**
     * Thêm vào giỏ hàng (AJAX)
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addToCart(@Valid @RequestBody AddToCartRequest request,
                                       HttpSession session) {
        try {
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

            if (khachHang == null) {
                return ResponseEntity.status(401).body(
                        Map.of("success", false, "message", "Vui lòng đăng nhập")
                );
            }

            CartItemDTO cartItem = cartService.addToCart(khachHang, request);
            Integer cartCount = cartService.getCartCount(khachHang);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã thêm vào giỏ hàng");
            response.put("cartItem", cartItem);
            response.put("cartCount", cartCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error adding to cart", e);
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }

    /**
     * Cập nhật số lượng (AJAX)
     */
    @PutMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateCartItem(@Valid @RequestBody UpdateCartRequest request,
                                            HttpSession session) {
        try {
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

            if (khachHang == null) {
                return ResponseEntity.status(401).body(
                        Map.of("success", false, "message", "Vui lòng đăng nhập")
                );
            }

            CartItemDTO cartItem = cartService.updateCartItem(khachHang, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã cập nhật");
            response.put("cartItem", cartItem);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error updating cart", e);
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }

    /**
     * Xóa sản phẩm (AJAX)
     */
    @DeleteMapping("/delete/{gioHangId}")
    @ResponseBody
    public ResponseEntity<?> deleteCartItem(@PathVariable Integer gioHangId,
                                            HttpSession session) {
        try {
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

            if (khachHang == null) {
                return ResponseEntity.status(401).body(
                        Map.of("success", false, "message", "Vui lòng đăng nhập")
                );
            }

            cartService.deleteCartItem(khachHang, gioHangId);
            Integer cartCount = cartService.getCartCount(khachHang);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã xóa sản phẩm");
            response.put("cartCount", cartCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error deleting cart item", e);
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }

    /**
     * Xóa nhiều sản phẩm (AJAX)
     */
    @DeleteMapping("/delete-multiple")
    @ResponseBody
    public ResponseEntity<?> deleteCartItems(@RequestBody List<Integer> gioHangIds,
                                             HttpSession session) {
        try {
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

            if (khachHang == null) {
                return ResponseEntity.status(401).body(
                        Map.of("success", false, "message", "Vui lòng đăng nhập")
                );
            }

            cartService.deleteCartItems(khachHang, gioHangIds);
            Integer cartCount = cartService.getCartCount(khachHang);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã xóa " + gioHangIds.size() + " sản phẩm");
            response.put("cartCount", cartCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error deleting cart items", e);
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }

    /**
     * Lấy số lượng giỏ hàng (AJAX)
     */
    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<?> getCartCount(HttpSession session) {
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

        if (khachHang == null) {
            return ResponseEntity.ok(Map.of("cartCount", 0));
        }

        Integer cartCount = cartService.getCartCount(khachHang);
        return ResponseEntity.ok(Map.of("cartCount", cartCount));
    }
}