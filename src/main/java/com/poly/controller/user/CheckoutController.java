package com.poly.controller.user;

import com.poly.dto.CheckoutRequest;
import com.poly.dto.OrderResponse;
import com.poly.entity.DiaChi;
import com.poly.entity.KhachHang;
import com.poly.repository.DiaChiRepository;
import com.poly.service.CartService;
import com.poly.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

    private final OrderService orderService;
    private final CartService cartService;
    private final DiaChiRepository diaChiRepository;

    /**
     * Hiển thị trang checkout
     */
    @GetMapping
    public String viewCheckout(@RequestParam(required = false) List<Integer> items,
                               HttpSession session,
                               Model model) {
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

        if (khachHang == null) {
            return "redirect:/login";
        }

        // Lấy địa chỉ của khách hàng
        List<DiaChi> diaChiList = diaChiRepository
                .findByKhachHangOrderByMacDinhDesc(khachHang);

        model.addAttribute("diaChiList", diaChiList);
        model.addAttribute("cartItemIds", items);

        return "user/checkout";
    }

    /**
     * Đặt hàng (AJAX)
     */
    @PostMapping("/place-order")
    @ResponseBody
    public ResponseEntity<?> placeOrder(@Valid @RequestBody CheckoutRequest request,
                                        HttpSession session) {
        try {
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");

            if (khachHang == null) {
                return ResponseEntity.status(401).body(
                        Map.of("success", false, "message", "Vui lòng đăng nhập")
                );
            }

            OrderResponse order = orderService.createOrder(khachHang, request);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đặt hàng thành công",
                    "order", order
            ));

        } catch (Exception e) {
            log.error("Error placing order", e);
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }
}