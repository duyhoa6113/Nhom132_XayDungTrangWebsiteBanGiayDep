package com.poly.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller xử lý giỏ hàng
 *
 * @author Nhóm 132
 */
@Controller
public class CartController {

    /**
     * Hiển thị trang giỏ hàng
     * URL: /cart
     */
    @GetMapping("/cart")
    public String cart(Model model) {

        model.addAttribute("pageTitle", "Giỏ hàng");

        return "user/cart";
    }
}