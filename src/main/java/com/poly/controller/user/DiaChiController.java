package com.poly.controller.user;

import com.poly.dto.DiaChiDTO;
import com.poly.dto.DiaChiResponseDTO;
import com.poly.entity.DiaChi;
import com.poly.service.DiaChiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dia-chi")
public class DiaChiController {

    @Autowired
    private DiaChiService diaChiService;

    /**
     * Lấy tất cả địa chỉ của khách hàng - TRẢ VỀ DTO
     */
    @GetMapping("/list/{khachHangId}")
    public ResponseEntity<?> getAllAddresses(@PathVariable Integer khachHangId) {
        try {
            List<DiaChi> addresses = diaChiService.getAddressesByKhachHangId(khachHangId);
            // Convert sang DTO để tránh circular reference
            List<DiaChiResponseDTO> dtos = addresses.stream()
                    .map(DiaChiResponseDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Lấy chi tiết một địa chỉ - TRẢ VỀ DTO
     */
    @GetMapping("/{diaChiId}")
    public ResponseEntity<?> getAddress(@PathVariable Integer diaChiId) {
        try {
            DiaChi address = diaChiService.getAddressById(diaChiId);
            // Convert sang DTO
            DiaChiResponseDTO dto = new DiaChiResponseDTO(address);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Thêm địa chỉ mới - TRẢ VỀ DTO
     */
    @PostMapping("/add/{khachHangId}")
    public ResponseEntity<?> addAddress(
            @PathVariable Integer khachHangId,
            @Valid @RequestBody DiaChiDTO diaChiDTO) {
        try {
            DiaChi savedAddress = diaChiService.addAddress(khachHangId, diaChiDTO);
            DiaChiResponseDTO dto = new DiaChiResponseDTO(savedAddress);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Cập nhật địa chỉ - TRẢ VỀ DTO
     */
    @PutMapping("/update/{khachHangId}")
    public ResponseEntity<?> updateAddress(
            @PathVariable Integer khachHangId,
            @Valid @RequestBody DiaChiDTO diaChiDTO) {
        try {
            DiaChi updatedAddress = diaChiService.updateAddress(khachHangId, diaChiDTO);
            DiaChiResponseDTO dto = new DiaChiResponseDTO(updatedAddress);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{khachHangId}/{diaChiId}")
    public ResponseEntity<?> deleteAddress(
            @PathVariable Integer khachHangId,
            @PathVariable Integer diaChiId) {
        try {
            diaChiService.deleteAddress(khachHangId, diaChiId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa địa chỉ thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/set-default/{khachHangId}/{diaChiId}")
    public ResponseEntity<?> setDefaultAddress(
            @PathVariable Integer khachHangId,
            @PathVariable Integer diaChiId) {
        try {
            diaChiService.setDefaultAddress(khachHangId, diaChiId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã đặt làm địa chỉ mặc định");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }
}