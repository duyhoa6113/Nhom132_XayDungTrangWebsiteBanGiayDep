package com.poly.controller.admin;

import com.poly.dto.AdminOrderDTO;
import com.poly.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderManagerController {

    private final AdminOrderService adminOrderService;

    /**
     * Hiển thị trang quản lý đơn hàng
     */
    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(value = "trangThai", required = false) String trangThai,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        
        model.addAttribute("page", "orders");
        
        // Giới hạn size hợp lý
        if (size < 5) size = 5;
        if (size > 100) size = 100;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AdminOrderDTO> ordersPage;
        
        // Lọc theo trạng thái
        if (trangThai != null && !trangThai.isEmpty()) {
            ordersPage = adminOrderService.getOrdersByStatus(trangThai, pageable);
        } else {
            ordersPage = adminOrderService.getAllOrders(pageable);
        }
        
        // Lấy thống kê
        Map<String, Long> stats = adminOrderService.getOrderStatistics();
        
        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        model.addAttribute("totalElements", ordersPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("selectedTrangThai", trangThai);
        
        // Thống kê
        model.addAttribute("totalOrders", stats.getOrDefault("totalOrders", 0L));
        model.addAttribute("choXuLy", stats.getOrDefault("choXuLy", 0L));
        model.addAttribute("daXacNhan", stats.getOrDefault("daXacNhan", 0L));
        model.addAttribute("dangGiao", stats.getOrDefault("dangGiao", 0L));
        model.addAttribute("hoanThanh", stats.getOrDefault("hoanThanh", 0L));
        model.addAttribute("daHuy", stats.getOrDefault("daHuy", 0L));
        
        return "admin/orders/index";
    }

    /**
     * API: Lấy đơn hàng theo ID
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> getOrderById(@PathVariable Integer id) {
        try {
            AdminOrderDTO order = adminOrderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error getting order by ID: {}", id, e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * API: Cập nhật trạng thái đơn hàng
     */
    @PutMapping("/api/{id}/status")
    @ResponseBody
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Integer id,
            @RequestParam String trangThai) {
        try {
            AdminOrderDTO updated = adminOrderService.updateOrderStatus(id, trangThai);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cập nhật trạng thái đơn hàng thành công",
                "order", updated
            ));
        } catch (Exception e) {
            log.error("Error updating order status: orderId={}, status={}", id, trangThai, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * API: Lấy thống kê đơn hàng
     */
    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getStatistics() {
        Map<String, Long> stats = adminOrderService.getOrderStatistics();
        return ResponseEntity.ok(stats);
    }
}
