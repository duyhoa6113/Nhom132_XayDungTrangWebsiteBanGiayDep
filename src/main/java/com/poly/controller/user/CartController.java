package com.poly.controller.user;

import com.poly.dto.AddToCartRequest;
import com.poly.dto.CartSummaryDTO;
import com.poly.dto.UpdateCartItemRequest;
import com.poly.entity.KhachHang;
import com.poly.repository.KhachHangRepository;
import com.poly.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller cho chức năng giỏ hàng
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;
    private final KhachHangRepository khachHangRepository;

    /**
     * Lấy thông tin giỏ hàng
     * GET /api/cart
     */
    @GetMapping
    public ResponseEntity<?> getCart() {
        try {
            Integer khachHangId = getCurrentKhachHangId();
            CartSummaryDTO cart = cartService.getCart(khachHangId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("Error getting cart: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     * POST /api/cart/add
     */
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@Valid @RequestBody AddToCartRequest request) {
        try {
            Integer khachHangId = getCurrentKhachHangId();
            CartSummaryDTO cart = cartService.addToCart(khachHangId, request);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("Error adding to cart: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     * PUT /api/cart/items/{cartItemId}
     */
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Integer cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        try {
            Integer khachHangId = getCurrentKhachHangId();
            CartSummaryDTO cart = cartService.updateCartItem(khachHangId, cartItemId, request);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("Error updating cart item: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     * DELETE /api/cart/items/{cartItemId}
     */
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<?> removeCartItem(@PathVariable Integer cartItemId) {
        try {
            Integer khachHangId = getCurrentKhachHangId();
            CartSummaryDTO cart = cartService.removeCartItem(khachHangId, cartItemId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("Error removing cart item: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Xóa toàn bộ giỏ hàng
     * DELETE /api/cart/clear
     */
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart() {
        try {
            Integer khachHangId = getCurrentKhachHangId();
            cartService.clearCart(khachHangId);
            return ResponseEntity.ok(createSuccessResponse("Đã xóa toàn bộ giỏ hàng"));
        } catch (Exception e) {
            log.error("Error clearing cart: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Lấy số lượng sản phẩm trong giỏ hàng
     * GET /api/cart/count
     */
    @GetMapping("/count")
    public ResponseEntity<?> getCartItemCount() {
        try {
            Integer khachHangId = getCurrentKhachHangId();
            Integer count = cartService.getCartItemCount(khachHangId);
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting cart count: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Lấy ID khách hàng từ authentication
     */
    private Integer getCurrentKhachHangId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // SỬA PHẦN NÀY
        KhachHang khachHang = khachHangRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        return khachHang.getKhachHangId();
    }

    /**
     * Tạo error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    /**
     * Tạo success response
     */
    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return response;
    }

}