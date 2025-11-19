package com.poly.controller.admin;

import com.poly.dto.UserDTO;
import com.poly.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    /**
     * Hiển thị trang quản lý người dùng
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("title", "Quản Lý Người Dùng & Vai Trò");
        return "admin/role/index";
    }

    /**
     * API: Lấy tất cả người dùng
     */
    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userManagementService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * API: Lấy người dùng theo vai trò
     */
    @GetMapping("/api/role/{role}")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        List<UserDTO> users = userManagementService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    /**
     * API: Lấy người dùng theo trạng thái
     */
    @GetMapping("/api/status/{status}")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> getUsersByStatus(@PathVariable String status) {
        List<UserDTO> users = userManagementService.getUsersByStatus(status);
        return ResponseEntity.ok(users);
    }

    /**
     * API: Tìm kiếm người dùng
     */
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String keyword) {
        List<UserDTO> users = userManagementService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }

    /**
     * API: Lọc người dùng theo nhiều điều kiện
     */
    @GetMapping("/api/filter")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> filterUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        List<UserDTO> users = userManagementService.filterUsers(role, status, keyword);
        return ResponseEntity.ok(users);
    }

    /**
     * API: Lấy thống kê
     */
    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getStatistics() {
        Map<String, Long> stats = userManagementService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * API: Thêm người dùng mới
     */
    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> payload) {
        try {
            UserDTO userDTO = mapToUserDTO(payload);
            String password = (String) payload.get("password");
            String role = userDTO.getRole();

            UserDTO created;
            if ("customer".equals(role)) {
                created = userManagementService.createCustomer(userDTO, password);
            } else {
                created = userManagementService.createEmployee(userDTO, password);
            }

            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * API: Cập nhật người dùng
     */
    @PutMapping("/api/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateUser(
            @PathVariable Integer id,
            @RequestParam String role,
            @RequestBody UserDTO userDTO) {
        try {
            UserDTO updated = userManagementService.updateUser(id, role, userDTO);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * API: Xóa người dùng
     */
    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(
            @PathVariable Integer id,
            @RequestParam String role) {
        try {
            userManagementService.deleteUser(id, role);
            return ResponseEntity.ok(Map.of("message", "Xóa người dùng thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Helper: Convert Map sang UserDTO
     */
    private UserDTO mapToUserDTO(Map<String, Object> payload) {
        UserDTO dto = new UserDTO();
        dto.setName((String) payload.get("name"));
        dto.setEmail((String) payload.get("email"));
        dto.setPhone((String) payload.get("phone"));
        dto.setRole((String) payload.get("role"));
        dto.setStatus((String) payload.get("status"));
        dto.setAddress((String) payload.get("address"));
        dto.setNotes((String) payload.get("notes"));
        dto.setGender((String) payload.get("gender"));

        return dto;
    }
}