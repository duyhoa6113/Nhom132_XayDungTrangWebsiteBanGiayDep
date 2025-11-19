package com.poly.service;

import com.poly.dto.UserDTO;
import com.poly.entity.KhachHang;
import com.poly.entity.NhanVien;
import com.poly.entity.VaiTro;
import com.poly.repository.KhachHangRepository;
import com.poly.repository.NhanVienRepository;
import com.poly.repository.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final NhanVienRepository nhanVienRepository;
    private final KhachHangRepository khachHangRepository;
    private final VaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Lấy tất cả người dùng (Nhân viên + Khách hàng)
     */
    public List<UserDTO> getAllUsers() {
        List<UserDTO> allUsers = new ArrayList<>();

        // Lấy tất cả nhân viên
        List<NhanVien> nhanViens = nhanVienRepository.findAll();
        for (NhanVien nv : nhanViens) {
            UserDTO dto = new UserDTO(
                    nv.getNhanVienId(),
                    nv.getHoTen(),
                    nv.getEmail(),
                    nv.getSdt(),
                    nv.getVaiTro().getVaiTroId(),
                    nv.getVaiTro().getTenVaiTro(),
                    nv.getChucVu(),
                    nv.getDiaChi(),
                    nv.getNgaySinh(),
                    nv.getGioiTinh(),
                    nv.getAvatar(),
                    nv.getTrangThai(),
                    nv.getCreatedAt(),
                    nv.getUpdatedAt()
            );
            allUsers.add(dto);
        }

        // Lấy tất cả khách hàng
        List<KhachHang> khachHangs = khachHangRepository.findAll();
        for (KhachHang kh : khachHangs) {
            UserDTO dto = new UserDTO(
                    kh.getKhachHangId(),
                    kh.getHoTen(),
                    kh.getEmail(),
                    kh.getSdt(),
                    null, // Khách hàng không có địa chỉ trong bảng KhachHang
                    kh.getNgaySinh(),
                    kh.getGioiTinh(),
                    kh.getAvatar(),
                    kh.getTrangThai(),
                    kh.getCreatedAt(),
                    kh.getUpdatedAt()
            );
            allUsers.add(dto);
        }

        return allUsers;
    }

    /**
     * Lấy người dùng theo vai trò
     */
    public List<UserDTO> getUsersByRole(String role) {
        List<UserDTO> users = new ArrayList<>();

        if ("admin".equals(role)) {
            List<NhanVien> admins = nhanVienRepository.findByVaiTroId(1);
            users = admins.stream().map(this::convertNhanVienToDTO).collect(Collectors.toList());
        } else if ("employee".equals(role)) {
            List<NhanVien> employees = nhanVienRepository.findByVaiTroId(2);
            users = employees.stream().map(this::convertNhanVienToDTO).collect(Collectors.toList());
        } else if ("customer".equals(role)) {
            List<KhachHang> customers = khachHangRepository.findAll();
            users = customers.stream().map(this::convertKhachHangToDTO).collect(Collectors.toList());
        }

        return users;
    }

    /**
     * Lấy người dùng theo trạng thái
     */
    public List<UserDTO> getUsersByStatus(String status) {
        Integer trangThai = "active".equals(status) ? 1 : 0;
        List<UserDTO> users = new ArrayList<>();

        // Nhân viên
        List<NhanVien> nhanViens = nhanVienRepository.findByTrangThai(trangThai);
        users.addAll(nhanViens.stream().map(this::convertNhanVienToDTO).collect(Collectors.toList()));

        // Khách hàng
        List<KhachHang> khachHangs = khachHangRepository.findByTrangThai(trangThai);
        users.addAll(khachHangs.stream().map(this::convertKhachHangToDTO).collect(Collectors.toList()));

        return users;
    }

    /**
     * Tìm kiếm người dùng
     */
    public List<UserDTO> searchUsers(String keyword) {
        List<UserDTO> users = new ArrayList<>();

        // Tìm nhân viên
        List<NhanVien> nhanViens = nhanVienRepository.searchByKeyword(keyword);
        users.addAll(nhanViens.stream().map(this::convertNhanVienToDTO).collect(Collectors.toList()));

        // Tìm khách hàng
        List<KhachHang> khachHangs = khachHangRepository.searchByKeyword(keyword);
        users.addAll(khachHangs.stream().map(this::convertKhachHangToDTO).collect(Collectors.toList()));

        return users;
    }

    /**
     * Lọc người dùng theo nhiều điều kiện
     */
    public List<UserDTO> filterUsers(String role, String status, String keyword) {
        List<UserDTO> users = getAllUsers();

        // Lọc theo role
        if (role != null && !"all".equals(role)) {
            users = users.stream()
                    .filter(u -> role.equals(u.getRole()))
                    .collect(Collectors.toList());
        }

        // Lọc theo status
        if (status != null && !"all".equals(status)) {
            users = users.stream()
                    .filter(u -> status.equals(u.getStatus()))
                    .collect(Collectors.toList());
        }

        // Tìm kiếm theo keyword
        if (keyword != null && !keyword.isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            users = users.stream()
                    .filter(u ->
                            u.getName().toLowerCase().contains(lowerKeyword) ||
                                    u.getEmail().toLowerCase().contains(lowerKeyword) ||
                                    (u.getPhone() != null && u.getPhone().contains(lowerKeyword))
                    )
                    .collect(Collectors.toList());
        }

        return users;
    }

    /**
     * Lấy thống kê
     */
    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();

        Long totalAdmins = nhanVienRepository.countByVaiTroId(1);
        Long totalEmployees = nhanVienRepository.countByVaiTroId(2);
        Long totalCustomers = (long) khachHangRepository.findAll().size();

        stats.put("totalUsers", totalAdmins + totalEmployees + totalCustomers);
        stats.put("totalAdmins", totalAdmins);
        stats.put("totalEmployees", totalEmployees);
        stats.put("totalCustomers", totalCustomers);

        return stats;
    }

    /**
     * Thêm nhân viên mới
     */
    @Transactional
    public UserDTO createEmployee(UserDTO userDTO, String password) {
        // Kiểm tra email đã tồn tại
        if (nhanVienRepository.existsByEmail(userDTO.getEmail()) ||
                khachHangRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email đã tồn tại: " + userDTO.getEmail());
        }

        // Lấy vai trò
        Integer vaiTroId = "admin".equals(userDTO.getRole()) ? 1 : 2;
        VaiTro vaiTro = vaiTroRepository.findById(vaiTroId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò"));

        NhanVien nhanVien = new NhanVien();
        nhanVien.setVaiTro(vaiTro);
        nhanVien.setHoTen(userDTO.getName());
        nhanVien.setEmail(userDTO.getEmail());
        nhanVien.setMatKhauHash(passwordEncoder.encode(password));
        nhanVien.setSdt(userDTO.getPhone());
        nhanVien.setDiaChi(userDTO.getAddress());
        nhanVien.setChucVu(userDTO.getNotes());
        nhanVien.setNgaySinh(userDTO.getDateOfBirth());
        nhanVien.setGioiTinh(userDTO.getGender());
        nhanVien.setTrangThai("active".equals(userDTO.getStatus()) ? 1 : 0);

        NhanVien saved = nhanVienRepository.save(nhanVien);
        return convertNhanVienToDTO(saved);
    }

    /**
     * Thêm khách hàng mới
     */
    @Transactional
    public UserDTO createCustomer(UserDTO userDTO, String password) {
        // Kiểm tra email đã tồn tại
        if (nhanVienRepository.existsByEmail(userDTO.getEmail()) ||
                khachHangRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email đã tồn tại: " + userDTO.getEmail());
        }

        KhachHang khachHang = new KhachHang();
        khachHang.setHoTen(userDTO.getName());
        khachHang.setEmail(userDTO.getEmail());
        khachHang.setMatKhauHash(passwordEncoder.encode(password));
        khachHang.setSdt(userDTO.getPhone());
        khachHang.setNgaySinh(userDTO.getDateOfBirth());
        khachHang.setGioiTinh(userDTO.getGender());
        khachHang.setTrangThai("active".equals(userDTO.getStatus()) ? 1 : 0);

        KhachHang saved = khachHangRepository.save(khachHang);
        return convertKhachHangToDTO(saved);
    }

    /**
     * Cập nhật người dùng
     */
    @Transactional
    public UserDTO updateUser(Integer id, String role, UserDTO userDTO) {
        if ("customer".equals(role)) {
            KhachHang kh = khachHangRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

            kh.setHoTen(userDTO.getName());
            kh.setSdt(userDTO.getPhone());
            kh.setNgaySinh(userDTO.getDateOfBirth());
            kh.setGioiTinh(userDTO.getGender());
            kh.setTrangThai("active".equals(userDTO.getStatus()) ? 1 : 0);

            KhachHang updated = khachHangRepository.save(kh);
            return convertKhachHangToDTO(updated);
        } else {
            NhanVien nv = nhanVienRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

            // Cập nhật vai trò nếu cần
            if (userDTO.getRole() != null) {
                Integer vaiTroId = "admin".equals(userDTO.getRole()) ? 1 : 2;
                VaiTro vaiTro = vaiTroRepository.findById(vaiTroId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò"));
                nv.setVaiTro(vaiTro);
            }

            nv.setHoTen(userDTO.getName());
            nv.setSdt(userDTO.getPhone());
            nv.setDiaChi(userDTO.getAddress());
            nv.setChucVu(userDTO.getNotes());
            nv.setNgaySinh(userDTO.getDateOfBirth());
            nv.setGioiTinh(userDTO.getGender());
            nv.setTrangThai("active".equals(userDTO.getStatus()) ? 1 : 0);

            NhanVien updated = nhanVienRepository.save(nv);
            return convertNhanVienToDTO(updated);
        }
    }

    /**
     * Xóa người dùng
     */
    @Transactional
    public void deleteUser(Integer id, String role) {
        if ("customer".equals(role)) {
            khachHangRepository.deleteById(id);
        } else {
            nhanVienRepository.deleteById(id);
        }
    }

    /**
     * Convert NhanVien sang UserDTO
     */
    private UserDTO convertNhanVienToDTO(NhanVien nv) {
        return new UserDTO(
                nv.getNhanVienId(),
                nv.getHoTen(),
                nv.getEmail(),
                nv.getSdt(),
                nv.getVaiTro().getVaiTroId(),
                nv.getVaiTro().getTenVaiTro(),
                nv.getChucVu(),
                nv.getDiaChi(),
                nv.getNgaySinh(),
                nv.getGioiTinh(),
                nv.getAvatar(),
                nv.getTrangThai(),
                nv.getCreatedAt(),
                nv.getUpdatedAt()
        );
    }

    /**
     * Convert KhachHang sang UserDTO
     */
    private UserDTO convertKhachHangToDTO(KhachHang kh) {
        return new UserDTO(
                kh.getKhachHangId(),
                kh.getHoTen(),
                kh.getEmail(),
                kh.getSdt(),
                null,
                kh.getNgaySinh(),
                kh.getGioiTinh(),
                kh.getAvatar(),
                kh.getTrangThai(),
                kh.getCreatedAt(),
                kh.getUpdatedAt()
        );
    }
}