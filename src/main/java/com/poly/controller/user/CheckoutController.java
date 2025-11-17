package com.poly.controller.user;

import com.poly.dto.CheckoutRequest;
import com.poly.entity.DiaChi;
import com.poly.entity.HoaDon;
import com.poly.entity.KhachHang;
import com.poly.service.CheckoutService;
import com.poly.service.DiaChiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CheckoutController - Xử lý thanh toán
 *
 * Chức năng:
 * - Hiển thị trang thanh toán với thông tin sản phẩm
 * - Quản lý địa chỉ giao hàng
 * - Xử lý đặt hàng
 * - Hiển thị trang đặt hàng thành công
 *
 * @author Nhóm 132
 */
@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final DiaChiService diaChiService;

    /**
     * HIỂN THỊ TRANG THANH TOÁN
     *
     * URL: GET /checkout?items=1,2,3
     *
     * Luồng xử lý:
     * 1. Kiểm tra đăng nhập
     * 2. Parse danh sách ID giỏ hàng từ query parameter
     * 3. Chuẩn bị dữ liệu checkout (sản phẩm, địa chỉ, phí ship)
     * 4. Trả về trang checkout
     */
    @GetMapping
    public String viewCheckout(
            @RequestParam(required = false) String items,
            HttpSession session,
            Model model) {

        log.info("=== BẮT ĐẦU XỬ LÝ CHECKOUT ===");

        // Bước 1: Kiểm tra đăng nhập
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");
        if (khachHang == null) {
            log.warn("Khách hàng chưa đăng nhập, chuyển hướng đến trang login");
            return "redirect:/login?redirect=/checkout";
        }

        try {
            // Bước 2: Parse danh sách ID giỏ hàng
            List<Integer> cartItemIds = null;
            if (items != null && !items.isEmpty()) {
                cartItemIds = Arrays.stream(items.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                log.info("Số sản phẩm được chọn: {}", cartItemIds.size());
            }

            // Bước 3: Chuẩn bị dữ liệu checkout
            var checkoutData = checkoutService.prepareCheckout(khachHang, cartItemIds);

            // Lấy danh sách địa chỉ của khách hàng
            List<DiaChi> addresses = diaChiService.getAddressesByCustomer(khachHang.getKhachHangId());


            // Tìm địa chỉ mặc định
            DiaChi defaultAddress = addresses.stream()
                    .filter(DiaChi::getMacDinh)
                    .findFirst()
                    .orElse(addresses.isEmpty() ? null : addresses.get(0));

            // Đưa dữ liệu vào model
            model.addAttribute("checkoutItems", checkoutData.getItems());
            model.addAttribute("subtotal", checkoutData.getSubtotal());
            model.addAttribute("shippingFee", checkoutData.getShippingFee());
            model.addAttribute("discount", checkoutData.getDiscount());
            model.addAttribute("totalAmount", checkoutData.getFinalAmount());
            model.addAttribute("addresses", addresses);
            model.addAttribute("defaultAddress", defaultAddress);
            model.addAttribute("cartItemIds", items);
            model.addAttribute("khachHangId", khachHang.getKhachHangId());

            log.info("Checkout page loaded successfully");
            log.info("Tổng tiền: {}", checkoutData.getFinalAmount());

            return "user/checkout";

        } catch (Exception e) {
            log.error("Lỗi khi tải trang checkout", e);
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    /**
     * XỬ LÝ ĐẶT HÀNG
     *
     * URL: POST /checkout/process
     *
     * Luồng xử lý:
     * 1. Validate thông tin đặt hàng
     * 2. Kiểm tra tồn kho
     * 3. Tạo đơn hàng (HoaDon)
     * 4. Tạo chi tiết đơn hàng (HoaDonChiTiet)
     * 5. Cập nhật tồn kho
     * 6. Xóa sản phẩm khỏi giỏ hàng
     * 7. Chuyển hướng đến trang thành công
     */
    @PostMapping("/process")
    public String processCheckout(
            @ModelAttribute CheckoutRequest request,
            HttpSession session,
            Model model) {

        log.info("=== BẮT ĐẦU XỬ LÝ ĐẶT HÀNG ===");

        // Kiểm tra đăng nhập
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");
        if (khachHang == null) {
            log.warn("Khách hàng chưa đăng nhập");
            return "redirect:/login";
        }

        try {
            log.info("Khách hàng: {} - {}", khachHang.getKhachHangId(), khachHang.getHoTen());
            log.info("Số sản phẩm đặt: {}", request.getCartItemIds().size());
            log.info("Phương thức thanh toán: {}", request.getPaymentMethod());

            // Tạo đơn hàng
            HoaDon hoaDon = checkoutService.createOrder(khachHang, request);

            log.info("✅ Đặt hàng thành công!");
            log.info("Mã đơn hàng: {}", hoaDon.getMaHoaDon());
            log.info("Tổng tiền: {}", hoaDon.getTongThanhToan());

            // Chuyển hướng đến trang thành công
            return "redirect:/checkout/success?orderId=" + hoaDon.getHoaDonId();

        } catch (IllegalArgumentException e) {
            log.error("❌ Lỗi validation: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "user/checkout";

        } catch (Exception e) {
            log.error("❌ Lỗi khi xử lý đặt hàng", e);
            model.addAttribute("error", "Có lỗi xảy ra khi đặt hàng. Vui lòng thử lại!");
            return "user/checkout";
        }
    }

    /**
     * HIỂN THỊ TRANG ĐẶT HÀNG THÀNH CÔNG
     *
     * URL: GET /checkout/success?orderId=123
     */
    @GetMapping("/success")
    public String orderSuccess(
            @RequestParam Integer orderId,
            HttpSession session,
            Model model) {

        log.info("=== HIỂN THỊ TRANG ĐẶT HÀNG THÀNH CÔNG ===");

        // Kiểm tra đăng nhập
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");
        if (khachHang == null) {
            return "redirect:/login";
        }

        try {
            // Lấy thông tin đơn hàng
            HoaDon hoaDon = checkoutService.getOrderById(orderId, khachHang);

            model.addAttribute("order", hoaDon);
            log.info("Hiển thị đơn hàng: {}", hoaDon.getMaHoaDon());

            return "user/order-success";

        } catch (Exception e) {
            log.error("Lỗi khi tải trang thành công", e);
            return "redirect:/orders";
        }
    }
}