package com.poly.service;

import com.poly.entity.KhachHang;
import com.poly.entity.SanPham;
import com.poly.entity.YeuThich;
import com.poly.repository.YeuThichRepository;
import com.poly.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class YeuThichService {

    private final YeuThichRepository yeuThichRepository;
    private final SanPhamRepository sanPhamRepository;

    /**
     * Thêm sản phẩm vào danh sách yêu thích
     */
    @Transactional
    public boolean themYeuThich(KhachHang khachHang, Integer sanPhamId) {
        try {
            // Kiểm tra sản phẩm có tồn tại không
            SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            // Kiểm tra đã yêu thích chưa
            if (yeuThichRepository.existsByKhachHangAndSanPham(khachHang, sanPham)) {
                log.info("Sản phẩm {} đã có trong danh sách yêu thích của khách hàng {}",
                        sanPhamId, khachHang.getKhachHangId());
                return false; // Đã tồn tại
            }

            // Tạo mới
            YeuThich yeuThich = new YeuThich();
            yeuThich.setKhachHang(khachHang);
            yeuThich.setSanPham(sanPham);

            yeuThichRepository.save(yeuThich);
            log.info("Đã thêm sản phẩm {} vào danh sách yêu thích của khách hàng {}",
                    sanPhamId, khachHang.getKhachHangId());
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi thêm sản phẩm vào yêu thích: ", e);
            return false;
        }
    }

    /**
     * Xóa sản phẩm khỏi danh sách yêu thích
     */
    @Transactional
    public boolean xoaYeuThich(KhachHang khachHang, Integer sanPhamId) {
        try {
            SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            yeuThichRepository.deleteByKhachHangAndSanPham(khachHang, sanPham);
            log.info("Đã xóa sản phẩm {} khỏi danh sách yêu thích của khách hàng {}",
                    sanPhamId, khachHang.getKhachHangId());
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi xóa sản phẩm khỏi yêu thích: ", e);
            return false;
        }
    }

    /**
     * Toggle yêu thích (thêm nếu chưa có, xóa nếu đã có)
     */
    @Transactional
    public boolean toggleYeuThich(KhachHang khachHang, Integer sanPhamId) {
        try {
            SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            if (yeuThichRepository.existsByKhachHangAndSanPham(khachHang, sanPham)) {
                // Đã có -> Xóa
                yeuThichRepository.deleteByKhachHangAndSanPham(khachHang, sanPham);
                log.info("Toggle: Đã xóa sản phẩm {} khỏi yêu thích", sanPhamId);
                return false; // Đã xóa
            } else {
                // Chưa có -> Thêm
                YeuThich yeuThich = new YeuThich();
                yeuThich.setKhachHang(khachHang);
                yeuThich.setSanPham(sanPham);
                yeuThichRepository.save(yeuThich);
                log.info("Toggle: Đã thêm sản phẩm {} vào yêu thích", sanPhamId);
                return true; // Đã thêm
            }
        } catch (Exception e) {
            log.error("Lỗi khi toggle yêu thích: ", e);
            throw new RuntimeException("Không thể thực hiện thao tác", e);
        }
    }

    /**
     * Lấy danh sách sản phẩm yêu thích
     */
    @Transactional(readOnly = true)
    public List<SanPham> layDanhSachYeuThich(KhachHang khachHang) {
        try {
            List<YeuThich> danhSachYeuThich = yeuThichRepository
                    .findByKhachHangOrderByCreatedAtDesc(khachHang);
            return danhSachYeuThich.stream()
                    .map(YeuThich::getSanPham)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách yêu thích: ", e);
            return List.of();
        }
    }

    /**
     * Lấy danh sách sản phẩm yêu thích theo ID khách hàng
     */
    @Transactional(readOnly = true)
    public List<SanPham> layDanhSachYeuThichTheoId(Integer khachHangId) {
        try {
            return yeuThichRepository.findSanPhamsByKhachHangId(khachHangId);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách yêu thích theo ID: ", e);
            return List.of();
        }
    }

    /**
     * Kiểm tra sản phẩm đã được yêu thích chưa
     */
    @Transactional(readOnly = true)
    public boolean kiemTraYeuThich(KhachHang khachHang, Integer sanPhamId) {
        try {
            SanPham sanPham = sanPhamRepository.findById(sanPhamId).orElse(null);
            if (sanPham == null) return false;
            return yeuThichRepository.existsByKhachHangAndSanPham(khachHang, sanPham);
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra yêu thích: ", e);
            return false;
        }
    }

    /**
     * Kiểm tra theo ID
     */
    @Transactional(readOnly = true)
    public boolean kiemTraYeuThichTheoId(Integer khachHangId, Integer sanPhamId) {
        try {
            return yeuThichRepository.existsByKhachHangIdAndSanPhamId(khachHangId, sanPhamId);
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra yêu thích theo ID: ", e);
            return false;
        }
    }

    /**
     * Đếm số lượng sản phẩm yêu thích
     */
    @Transactional(readOnly = true)
    public long demSoLuongYeuThich(KhachHang khachHang) {
        try {
            return yeuThichRepository.countByKhachHang(khachHang);
        } catch (Exception e) {
            log.error("Lỗi khi đếm số lượng yêu thích: ", e);
            return 0;
        }
    }

    /**
     * Đếm số lượng theo ID
     */
    @Transactional(readOnly = true)
    public long demSoLuongYeuThichTheoId(Integer khachHangId) {
        try {
            return yeuThichRepository.countByKhachHangId(khachHangId);
        } catch (Exception e) {
            log.error("Lỗi khi đếm số lượng yêu thích theo ID: ", e);
            return 0;
        }
    }

    /**
     * Lấy danh sách ID sản phẩm yêu thích
     */
    @Transactional(readOnly = true)
    public List<Integer> layDanhSachIdYeuThich(Integer khachHangId) {
        try {
            return yeuThichRepository.findSanPhamIdsByKhachHangId(khachHangId);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách ID yêu thích: ", e);
            return List.of();
        }
    }

    /**
     * Xóa tất cả yêu thích của khách hàng
     */
    @Transactional
    public boolean xoaTatCaYeuThich(KhachHang khachHang) {
        try {
            yeuThichRepository.deleteByKhachHang(khachHang);
            log.info("Đã xóa tất cả yêu thích của khách hàng {}", khachHang.getKhachHangId());
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi xóa tất cả yêu thích: ", e);
            return false;
        }
    }

    /**
     * Kiểm tra và lấy thông tin yêu thích
     */
    @Transactional(readOnly = true)
    public YeuThich layThongTinYeuThich(Integer khachHangId, Integer sanPhamId) {
        try {
            return yeuThichRepository.findByKhachHangIdAndSanPhamId(khachHangId, sanPhamId)
                    .orElse(null);
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin yêu thích: ", e);
            return null;
        }
    }
}
