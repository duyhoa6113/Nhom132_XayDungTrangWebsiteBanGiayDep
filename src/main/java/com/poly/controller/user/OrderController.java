package com.poly.controller.user;

import com.poly.entity.HoaDon;
import com.poly.entity.KhachHang;
import com.poly.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * OrderController - Quản lý đơn hàng của khách hàng
 *
 * @author Nhóm 132
 */
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    /**
     * HIỂN THỊ DANH SÁCH ĐƠN HÀNG
     * URL: GET /orders
     * URL: GET /orders?status=ChoXuLy&page=0
     */
    @GetMapping
    public String viewOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session,
            Model model) {

        log.info("=== XEM DANH SÁCH ĐƠN HÀNG ===");

        // Kiểm tra đăng nhập
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");
        if (khachHang == null) {
            log.warn("Khách hàng chưa đăng nhập");
            return "redirect:/login?redirect=/orders";
        }

        try {
            // Tạo pageable - sắp xếp theo ngày tạo mới nhất
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

            // Lấy danh sách đơn hàng
            Page<HoaDon> orders;
            if (status != null && !status.isEmpty()) {
                orders = orderService.getOrdersByStatus(khachHang, status, pageable);
            } else {
                orders = orderService.getAllOrders(khachHang, pageable);
            }

            // Đưa dữ liệu vào model
            model.addAttribute("orders", orders.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", orders.getTotalPages());
            model.addAttribute("totalItems", orders.getTotalElements());
            model.addAttribute("currentStatus", status);

            log.info("Tìm thấy {} đơn hàng", orders.getTotalElements());

            return "user/orders";

        } catch (Exception e) {
            log.error("Lỗi khi tải danh sách đơn hàng", e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải đơn hàng");
            return "user/orders";
        }
    }

    /**
     * XEM CHI TIẾT ĐƠN HÀNG
     * URL: GET /orders/{orderId}
     */
    @GetMapping("/{orderId}")
    public String viewOrderDetail(
            @PathVariable Integer orderId,
            HttpSession session,
            Model model) {

        log.info("=== XEM CHI TIẾT ĐƠN HÀNG {} ===", orderId);

        // Kiểm tra đăng nhập
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");
        if (khachHang == null) {
            return "redirect:/login";
        }

        try {
            HoaDon order = orderService.getOrderDetail(orderId, khachHang);
            model.addAttribute("order", order);

            log.info("Hiển thị chi tiết đơn hàng: {}", order.getMaHoaDon());

            return "user/order-detail";

        } catch (Exception e) {
            log.error("Lỗi khi tải chi tiết đơn hàng", e);
            model.addAttribute("error", e.getMessage());
            return "redirect:/orders";
        }
    }

    /**
     * HỦY ĐƠN HÀNG
     * URL: POST /orders/{orderId}/cancel
     */
    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(
            @PathVariable Integer orderId,
            HttpSession session,
            Model model) {

        log.info("=== HỦY ĐƠN HÀNG {} ===", orderId);

        // Kiểm tra đăng nhập
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");
        if (khachHang == null) {
            return "redirect:/login";
        }

        try {
            orderService.cancelOrder(orderId, khachHang);
            log.info("Hủy đơn hàng thành công: {}", orderId);
            return "redirect:/orders?success=cancel";

        } catch (Exception e) {
            log.error("Lỗi khi hủy đơn hàng", e);
            return "redirect:/orders?error=" + e.getMessage();
        }
    }
}