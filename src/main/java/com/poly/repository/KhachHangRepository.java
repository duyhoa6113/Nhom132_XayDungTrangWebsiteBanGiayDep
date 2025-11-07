package com.poly.repository;

import com.poly.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository để truy vấn dữ liệu khách hàng
 *
 * @author Nhóm 132
 */
@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {

    /**
     * Tìm khách hàng theo email
     *
     * @param email Email khách hàng
     * @return Optional chứa khách hàng
     */
    Optional<KhachHang> findByEmail(String email);

    /**
     * Tìm khách hàng theo email và trạng thái
     *
     * @param email Email
     * @param trangThai Trạng thái (1 = active, 0 = inactive)
     * @return Optional chứa khách hàng
     */
    Optional<KhachHang> findByEmailAndTrangThai(String email, Byte trangThai);

    /**
     * Tìm khách hàng theo số điện thoại
     *
     * @param sdt Số điện thoại
     * @return Optional chứa khách hàng
     */
    Optional<KhachHang> findBySdt(String sdt);

    /**
     * Kiểm tra email đã tồn tại chưa
     *
     * @param email Email cần kiểm tra
     * @return true nếu đã tồn tại
     */
    boolean existsByEmail(String email);

    /**
     * Kiểm tra số điện thoại đã tồn tại chưa
     *
     * @param sdt Số điện thoại
     * @return true nếu đã tồn tại
     */
    boolean existsBySdt(String sdt);

    /**
     * Lấy tất cả khách hàng đang hoạt động
     *
     * @param trangThai Trạng thái (1 = active)
     * @return List khách hàng
     */
    List<KhachHang> findByTrangThai(Byte trangThai);

    /**
     * Đếm số khách hàng đang hoạt động
     *
     * @param trangThai Trạng thái
     * @return Số lượng khách hàng
     */
    long countByTrangThai(Byte trangThai);

    /**
     * Tìm khách hàng theo reset token
     *
     * @param resetToken Token reset mật khẩu
     * @return Optional chứa khách hàng
     */
    Optional<KhachHang> findByResetToken(String resetToken);
}