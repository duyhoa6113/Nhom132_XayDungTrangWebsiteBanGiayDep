package com.poly.controller.admin;

import com.poly.dto.VaiTroDTO;
import com.poly.service.VaiTroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/role")
@RequiredArgsConstructor
public class RoleController {

    private final VaiTroService vaiTroService;

    /**
     * Hiển thị danh sách vai trò
     */
    @GetMapping
    public String index(Model model) {
        List<VaiTroDTO> roles = vaiTroService.getAllVaiTro();
        model.addAttribute("roles", roles);
        model.addAttribute("title", "Quản Lý Vai Trò");
        return "admin/role/index";
    }

    /**
     * Hiển thị form thêm vai trò mới
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("vaiTroDTO", new VaiTroDTO());
        model.addAttribute("title", "Thêm Vai Trò Mới");
        model.addAttribute("action", "create");
        return "admin/role/index";
    }

    /**
     * Xử lý thêm vai trò mới
     */
    @PostMapping("/create")
    public String createVaiTro(@Valid @ModelAttribute("vaiTroDTO") VaiTroDTO vaiTroDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Thêm Vai Trò Mới");
            model.addAttribute("action", "create");
            return "admin/role/index";
        }

        try {
            vaiTroService.createVaiTro(vaiTroDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm vai trò thành công!");
            return "redirect:/admin/role";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("title", "Thêm Vai Trò Mới");
            model.addAttribute("action", "create");
            return "admin/role/index";
        }
    }

    /**
     * Hiển thị form chỉnh sửa vai trò
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            VaiTroDTO vaiTroDTO = vaiTroService.getVaiTroById(id);
            model.addAttribute("vaiTroDTO", vaiTroDTO);
            model.addAttribute("title", "Chỉnh Sửa Vai Trò");
            model.addAttribute("action", "edit");
            return "admin/role/index";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/role";
        }
    }

    /**
     * Xử lý cập nhật vai trò
     */
    @PostMapping("/edit/{id}")
    public String updateVaiTro(@PathVariable("id") Integer id,
                               @Valid @ModelAttribute("vaiTroDTO") VaiTroDTO vaiTroDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Chỉnh Sửa Vai Trò");
            model.addAttribute("action", "edit");
            return "admin/role/index";
        }

        try {
            vaiTroService.updateVaiTro(id, vaiTroDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật vai trò thành công!");
            return "redirect:/admin/role";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("title", "Chỉnh Sửa Vai Trò");
            model.addAttribute("action", "edit");
            return "admin/role/index";
        }
    }

    /**
     * Xóa vai trò
     */
    @PostMapping("/delete/{id}")
    public String deleteVaiTro(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            vaiTroService.deleteVaiTro(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa vai trò thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/role";
    }

    /**
     * Xem chi tiết vai trò
     */
    @GetMapping("/detail/{id}")
    public String showDetail(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            VaiTroDTO vaiTroDTO = vaiTroService.getVaiTroById(id);
            model.addAttribute("vaiTro", vaiTroDTO);
            model.addAttribute("title", "Chi Tiết Vai Trò");
            return "admin/role/index";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/role";
        }
    }
}