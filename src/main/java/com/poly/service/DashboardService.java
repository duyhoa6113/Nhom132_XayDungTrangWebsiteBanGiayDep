package com.poly.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poly.dto.DashboardStatsDTO;
import com.poly.dto.RecentOrderDTO;
import com.poly.dto.RevenueChartDTO;
import com.poly.dto.TopProductDTO;
import com.poly.entity.HoaDon;
import com.poly.entity.KhachHang;
import com.poly.repository.HoaDonChiTietRepository;
import com.poly.repository.HoaDonRepository;
import com.poly.repository.SanPhamRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service cho dashboard admin
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final HoaDonRepository hoaDonRepository;
    private final SanPhamRepository sanPhamRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;

    /**
     * Lấy tất cả thống kê dashboard
     */
    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        log.info("=== LẤY DỮ LIỆU DASHBOARD ===");

        DashboardStatsDTO stats = new DashboardStatsDTO();

        try {
            // Tính tổng doanh thu
            BigDecimal currentMonthRevenue = hoaDonRepository.getTotalRevenueCurrentMonth();
            BigDecimal previousMonthRevenue = hoaDonRepository.getTotalRevenuePreviousMonth();
            if (currentMonthRevenue == null) currentMonthRevenue = BigDecimal.ZERO;
            if (previousMonthRevenue == null) previousMonthRevenue = BigDecimal.ZERO;
            stats.setTotalRevenue(currentMonthRevenue);
            stats.setPreviousMonthRevenue(previousMonthRevenue);
            log.info("✅ Revenue loaded: Current={}, Previous={}", currentMonthRevenue, previousMonthRevenue);
        } catch (Exception e) {
            log.error("❌ Error loading revenue: {}", e.getMessage(), e);
            stats.setTotalRevenue(BigDecimal.ZERO);
            stats.setPreviousMonthRevenue(BigDecimal.ZERO);
        }

        try {
            // Tổng số đơn hàng
            Long totalOrders = hoaDonRepository.countOrdersCurrentMonth();
            if (totalOrders == null) totalOrders = 0L;
            stats.setTotalOrders(totalOrders);
            log.info("✅ Orders loaded: {}", totalOrders);
        } catch (Exception e) {
            log.error("❌ Error loading orders count: {}", e.getMessage(), e);
            stats.setTotalOrders(0L);
        }

        try {
            // Tổng số sản phẩm
            Long totalProducts = sanPhamRepository.countActiveProducts();
            if (totalProducts == null) totalProducts = 0L;
            stats.setTotalProducts(totalProducts);
            log.info("✅ Products loaded: {}", totalProducts);
        } catch (Exception e) {
            log.error("❌ Error loading products count: {}", e.getMessage(), e);
            stats.setTotalProducts(0L);
        }

        try {
            // Dữ liệu biểu đồ doanh thu
            List<RevenueChartDTO> revenueChart = getRevenueChartData();
            stats.setRevenueChart(revenueChart);
            log.info("✅ Revenue chart loaded: {} months", revenueChart.size());
        } catch (Exception e) {
            log.error("❌ Error loading revenue chart: {}", e.getMessage(), e);
            stats.setRevenueChart(new ArrayList<>());
        }

        try {
            // Top 10 sản phẩm bán chạy
            List<TopProductDTO> topProducts = getTopProducts();
            stats.setTopProducts(topProducts);
            log.info("✅ Top products loaded: {}", topProducts.size());
        } catch (Exception e) {
            log.error("❌ Error loading top products: {}", e.getMessage(), e);
            stats.setTopProducts(new ArrayList<>());
        }

        try {
            // 5 đơn hàng gần đây
            List<RecentOrderDTO> recentOrders = getRecentOrders();
            stats.setRecentOrders(recentOrders);
            log.info("✅ Recent orders loaded: {}", recentOrders.size());
        } catch (Exception e) {
            log.error("❌ Error loading recent orders: {}", e.getMessage(), e);
            stats.setRecentOrders(new ArrayList<>());
        }

        log.info("✅ Dashboard stats retrieved successfully: Revenue={}, Orders={}, Products={}",
                stats.getTotalRevenue(), stats.getTotalOrders(), stats.getTotalProducts());

        return stats;
    }

    /**
     * Lấy dữ liệu biểu đồ doanh thu 12 tháng gần nhất
     */
    @Transactional(readOnly = true)
    public List<RevenueChartDTO> getRevenueChartData() {
        List<Object[]> results = hoaDonRepository.getRevenueByMonthLast12Months();

        // Tạo map từ kết quả query
        Map<String, BigDecimal> revenueMap = new HashMap<>();
        for (Object[] row : results) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            BigDecimal revenue = (BigDecimal) row[2];
            String key = year + "-" + String.format("%02d", month);
            revenueMap.put(key, revenue);
        }

        // Tạo danh sách 12 tháng gần nhất
        LocalDateTime now = LocalDateTime.now();
        List<RevenueChartDTO> chartData = new ArrayList<>();

        for (int i = 11; i >= 0; i--) {
            LocalDateTime date = now.minusMonths(i);
            int year = date.getYear();
            int monthNum = date.getMonthValue();
            String key = year + "-" + String.format("%02d", monthNum);

            BigDecimal revenue = revenueMap.getOrDefault(key, BigDecimal.ZERO);
            Month month = Month.of(monthNum);
            String monthName = month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String monthFullName = month.getDisplayName(TextStyle.FULL, new Locale("vi", "VN"));

            RevenueChartDTO dto = new RevenueChartDTO(
                    monthName,
                    monthFullName,
                    revenue,
                    year,
                    monthNum
            );
            chartData.add(dto);
        }

        return chartData;
    }

    /**
     * Lấy top 10 sản phẩm bán chạy
     */
    @Transactional(readOnly = true)
    public List<TopProductDTO> getTopProducts() {
        List<Object[]> results = hoaDonChiTietRepository.findTop10BestSellingProducts();

        List<TopProductDTO> topProducts = new ArrayList<>();
        int rank = 1;

        for (Object[] row : results) {
            Integer sanPhamId = ((Number) row[0]).intValue();
            String ten = (String) row[1];
            Long soLuongBan = ((Number) row[2]).longValue();

            TopProductDTO dto = new TopProductDTO(sanPhamId, ten, soLuongBan, rank++);
            topProducts.add(dto);
        }

        // Đảm bảo có đủ 10 items (fill với null nếu không đủ)
        while (topProducts.size() < 10) {
            topProducts.add(new TopProductDTO(null, "N/A", 0L, topProducts.size() + 1));
        }

        return topProducts;
    }

    /**
     * Lấy 5 đơn hàng gần đây nhất
     */
    @Transactional(readOnly = true)
    public List<RecentOrderDTO> getRecentOrders() {
        Pageable pageable = PageRequest.of(0, 5);
        List<HoaDon> orders = hoaDonRepository.findTop5ByOrderByCreatedAtDesc(pageable);

        return orders.stream()
                .map(order -> {
                    RecentOrderDTO dto = new RecentOrderDTO();
                    dto.setHoaDonId(order.getHoaDonId());
                    dto.setMaHoaDon(order.getMaHoaDon());
                    
                    // Sử dụng hoTenNhan từ đơn hàng (người nhận) hoặc tên khách hàng
                    String hoTenNhan = order.getHoTenNhan();
                    if (hoTenNhan != null && !hoTenNhan.trim().isEmpty()) {
                        dto.setHoTenKhachHang(hoTenNhan);
                    } else {
                        try {
                            KhachHang kh = order.getKhachHang();
                            if (kh != null) {
                                String hoTen = kh.getHoTen();
                                dto.setHoTenKhachHang(hoTen != null ? hoTen : "N/A");
                            } else {
                                dto.setHoTenKhachHang("N/A");
                            }
                        } catch (Exception e) {
                            log.warn("Error fetching khách hàng for order {}: {}", order.getHoaDonId(), e.getMessage());
                            dto.setHoTenKhachHang("N/A");
                        }
                    }
                    
                    dto.setTongThanhToan(order.getTongThanhToan());
                    dto.setCreatedAt(order.getCreatedAt());
                    dto.setTrangThai(order.getTrangThai());
                    dto.setTrangThaiThanhToan(order.getTrangThaiThanhToan());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

