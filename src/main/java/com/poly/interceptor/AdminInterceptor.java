package com.poly.interceptor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.poly.entity.NhanVien;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * Interceptor để bảo vệ các routes admin
 * Chỉ cho phép nhân viên có VaiTroId = 1 (Admin) truy cập
 */
@Component
@Slf4j
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler) throws Exception {
        
        HttpSession session = request.getSession(false);
        
        // Kiểm tra session có tồn tại không
        if (session == null) {
            log.warn("Access denied: No session - {}", request.getRequestURI());
            response.sendRedirect("/login?redirect=" + request.getRequestURI());
            return false;
        }
        
        // Kiểm tra có nhân viên trong session không
        NhanVien nhanVien = (NhanVien) session.getAttribute("nhanVien");
        if (nhanVien == null) {
            log.warn("Access denied: Not logged in as employee - {}", request.getRequestURI());
            response.sendRedirect("/login?redirect=" + request.getRequestURI());
            return false;
        }
        
        // Kiểm tra có phải Admin không (VaiTroId = 1)
        boolean isAdmin = nhanVien.getVaiTro() != null && 
                         nhanVien.getVaiTro().getVaiTroId() == 1;
        
        if (!isAdmin) {
            log.warn("Access denied: Not an admin - User: {} - {}", 
                    nhanVien.getEmail(), request.getRequestURI());
            response.sendRedirect("/Index?error=access_denied");
            return false;
        }
        
        log.debug("Admin access granted: {} - {}", nhanVien.getEmail(), request.getRequestURI());
        return true;
    }
}

