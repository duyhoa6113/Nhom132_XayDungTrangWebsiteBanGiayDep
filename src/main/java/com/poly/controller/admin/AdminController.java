package com.poly.controller.admin;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.poly.dto.DashboardStatsDTO;
import com.poly.service.DashboardService;
import com.poly.service.UserManagementService;

import lombok.RequiredArgsConstructor;

/**
 * Router cơ bản cho các trang admin. Mỗi method set attribute "page" để sidebar
 * biết trang nào đang active.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DashboardService dashboardService;
    private final UserManagementService userManagementService;

    @GetMapping({"", "/", "/index", "/dashboard"})
    public String index(Model model) {
        model.addAttribute("page", "dashboard");
        return "admin/index";
    }

    /**
     * API endpoint để lấy dữ liệu dashboard (JSON)
     */
    @GetMapping("/api/dashboard/stats")
    @ResponseBody
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }


    @GetMapping("/admin/categories")
    public String categories(Model model) {
        return "/admin/categories/index";
    }

    // Route này đã được chuyển sang OrderManagerController
    // @GetMapping("/orders")
    // public String orders(Model model) {
    //     model.addAttribute("page", "orders");
    //     return "admin/orders/index";
    // }

    @GetMapping("/account")
    public String account(Model model) {
        model.addAttribute("page", "account");
        return "admin/account/index";
    }

    @GetMapping("/role")
    public String role(Model model) {
        model.addAttribute("page", "role");
        
        // Load statistics
        Map<String, Long> stats = userManagementService.getStatistics();
        model.addAttribute("totalUsers", stats.getOrDefault("totalUsers", 0L));
        model.addAttribute("totalAdmins", stats.getOrDefault("totalAdmins", 0L));
        model.addAttribute("totalEmployees", stats.getOrDefault("totalEmployees", 0L));
        model.addAttribute("totalCustomers", stats.getOrDefault("totalCustomers", 0L));
        
        return "admin/role/index";
    }

}
