package com.poly.controller.user;

import com.poly.dto.CheckoutRequest;
import com.poly.dto.MoMoPaymentResponse;
import com.poly.entity.DiaChi;
import com.poly.entity.HoaDon;
import com.poly.entity.KhachHang;
import com.poly.service.CheckoutService;
import com.poly.service.DiaChiService;
import com.poly.service.MoMoService;
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
 */
@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final DiaChiService diaChiService;
    private final MoMoService moMoService;

    /**
     * HIỂN THỊ TRANG THANH TOÁN
     */
    @GetMapping
    public String viewCheckout(
            @RequestParam(required = false) String items,
            HttpSession session,
            Model model) {

        log.info("=== BẮT ĐẦU XỬ LÝ CHECKOUT ===");

        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");
        if (khachHang == null) {
            log.warn("Khách hàng chưa đăng nhập, chuyển hướng đến trang login");
            return "redirect:/login?redirect=/checkout";
        }

        try {
            List<Integer> cartItemIds = null;
            if (items != null && !items.isEmpty()) {
                cartItemIds = Arrays.stream(items.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                log.info("Số sản phẩm được chọn: {}", cartItemIds.size());
            }

            var checkoutData = checkoutService.prepareCheckout(khachHang, cartItemIds);
            List<DiaChi> addresses = diaChiService.getAddressesByCustomer(khachHang.getKhachHangId());

            DiaChi defaultAddress = addresses.stream()
                    .filter(DiaChi::getMacDinh)
                    .findFirst()
                    .orElse(addresses.isEmpty() ? null : addresses.get(0));

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
            return "user/checkout";

        } catch (Exception e) {
            log.error("Lỗi khi tải trang checkout", e);
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    /**
     * XỬ LÝ ĐẶT HÀNG (CÓ HỖ TRỢ MOMO)
     */
    @PostMapping("/process")
    public String processCheckout(
            @ModelAttribute CheckoutRequest request,
            HttpSession session,
            Model model) {

        log.info("=== BẮT ĐẦU XỬ LÝ ĐẶT HÀNG ===");

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

            // **KIỂM TRA PHƯƠNG THỨC THANH TOÁN**
            if ("MOMO".equalsIgnoreCase(request.getPaymentMethod())) {
                log.info("=== CHUYỂN HƯỚNG ĐẾN THANH TOÁN MOMO ===");

                String orderInfo = "Thanh toán đơn hàng " + hoaDon.getMaHoaDon();
                long amount = hoaDon.getTongThanhToan().longValue();

                MoMoPaymentResponse momoResponse = moMoService.createPayment(
                        hoaDon.getMaHoaDon(),
                        amount,
                        orderInfo
                );

                if (momoResponse.getResultCode() == 0) {
                    session.setAttribute("pendingOrderId", hoaDon.getHoaDonId());
                    session.setAttribute("pendingOrderCode", hoaDon.getMaHoaDon());

                    log.info("✅ Chuyển hướng đến MoMo: {}", momoResponse.getPayUrl());
                    return "redirect:" + momoResponse.getPayUrl();
                } else {
                    log.error("❌ Lỗi tạo thanh toán MoMo: {}", momoResponse.getMessage());
                    model.addAttribute("error", "Không thể tạo thanh toán MoMo: " + momoResponse.getMessage());
                    return "user/checkout";
                }
            }

            // Nếu COD hoặc phương thức khác
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
     * XỬ LÝ CALLBACK TỪ MOMO
     */
    @GetMapping("/momo/callback")
    public String momoCallback(
            @RequestParam(required = false) String partnerCode,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String requestId,
            @RequestParam(required = false) Integer resultCode,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) Long amount,
            @RequestParam(required = false) String orderInfo,
            @RequestParam(required = false) String responseTime,
            @RequestParam(required = false) String extraData,
            @RequestParam(required = false) String signature,
            HttpSession session,
            Model model) {

        log.info("=== MOMO CALLBACK ===");
        log.info("Result Code: {}", resultCode);
        log.info("Order ID: {}", orderId);
        log.info("Message: {}", message);
        log.info("Amount: {}", amount);

        try {
            Integer hoaDonId = (Integer) session.getAttribute("pendingOrderId");

            if (hoaDonId == null) {
                log.error("❌ Không tìm thấy order ID trong session");
                model.addAttribute("error", "Không tìm thấy thông tin đơn hàng");
                return "user/payment-error";
            }

            if (resultCode != null && resultCode == 0) {
                log.info("✅ Thanh toán MoMo thành công!");

                checkoutService.updatePaymentStatus(hoaDonId, "PAID");

                session.removeAttribute("pendingOrderId");
                session.removeAttribute("pendingOrderCode");

                return "redirect:/checkout/success?orderId=" + hoaDonId;

            } else {
                log.error("❌ Thanh toán MoMo thất bại: {}", message);

                checkoutService.updatePaymentStatus(hoaDonId, "FAILED");

                model.addAttribute("error", "Thanh toán thất bại: " + message);
                model.addAttribute("orderId", orderId);

                return "user/payment-error";
            }

        } catch (Exception e) {
            log.error("❌ Lỗi khi xử lý callback MoMo", e);
            model.addAttribute("error", "Có lỗi xảy ra khi xử lý thanh toán");
            return "user/payment-error";
        }
    }

    /**
     * XỬ LÝ IPN TỪ MOMO
     */
    @PostMapping("/momo/ipn")
    @ResponseBody
    public String momoIPN(@RequestBody String requestBody) {
        log.info("=== MOMO IPN ===");
        log.info("Request Body: {}", requestBody);
        return "{\"status\": \"success\"}";
    }

    /**
     * HIỂN THỊ TRANG ĐẶT HÀNG THÀNH CÔNG
     */
    @GetMapping("/success")
    public String orderSuccess(
            @RequestParam Integer orderId,
            HttpSession session,
            Model model) {

        log.info("=== HIỂN THỊ TRANG ĐẶT HÀNG THÀNH CÔNG ===");

        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");
        if (khachHang == null) {
            return "redirect:/login";
        }

        try {
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