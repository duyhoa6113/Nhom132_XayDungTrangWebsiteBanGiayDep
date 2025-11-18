package com.poly.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Router cơ bản cho các trang admin. Mỗi method set attribute "page" để sidebar
 * biết trang nào đang active.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping({"", "/", "/index", "/dashboard"})
    public String index(Model model) {
        model.addAttribute("page", "dashboard");
        return "admin/index";
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("page", "products");
        return "admin/products/index";
    }

    @GetMapping("/admin/categories")
    public String categories(Model model) {
        return "/admin/categories/index";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("page", "orders");
        return "admin/orders/index";
    }

    @GetMapping("/account")
    public String account(Model model) {
        model.addAttribute("page", "account");
        return "admin/account/index";
    }

    @GetMapping("/role")
    public String role(Model model) {
        model.addAttribute("page", "role");
        return "admin/role/index";
    }
}
