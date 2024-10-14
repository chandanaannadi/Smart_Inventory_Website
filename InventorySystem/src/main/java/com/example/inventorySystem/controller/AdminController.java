package com.example.inventorySystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.inventorySystem.dto.MonthlySaleDto;
import com.example.inventorySystem.dto.UserDto;
import com.example.inventorySystem.dto.forms.*;
import com.example.inventorySystem.service.AuthService;
import com.example.inventorySystem.service.ProductService;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    AuthService authService;
    @Autowired
    ProductService productService;

    /* Dashboard Related functions */
    
    @GetMapping("/admin/dashboard")
    public String getDashboard(HttpServletRequest request, HttpSession httpSession, HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";
        if (userDto != null) {
            httpSession.setAttribute("session-token", userDto.getSessionToken());
            return "Admin-dashboard";
        }
        return "Login";
    }
    
    @GetMapping("/admin/monthly-orders")
    @ResponseBody
    public List<MonthlySaleDto> getMonthlySales(HttpServletRequest request, HttpSession httpSession, HttpServletResponse response, Model model) {
        return productService.getMonthlyOrders();
    }
    

    /* Product Related functions */
    
    @GetMapping("/admin/product")
    public String getProductPage(HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";
        model.addAttribute("products", productService.adminWarehouseProducts(userDto.getId()));
        model.addAttribute("users", authService.getAllActiveUsers());
        model.addAttribute("success", "null");
        model.addAttribute("message", "");

        return "Admin-Product";
    }

    @PostMapping("/admin/add-product")
    public String addProduct(@ModelAttribute(name = "AddProductForm") ProductForm addProductForm,
                             @RequestParam("file") MultipartFile file,
                             HttpServletRequest request,
                             HttpSession httpSession, HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";

        try {
            productService.addProduct(addProductForm, file, userDto.getWarehouseId());
            model.addAttribute("products", productService.adminWarehouseProducts(userDto.getId()));
            model.addAttribute("users", authService.getAllActiveUsers());
            model.addAttribute("success", true);
            model.addAttribute("message", "Product added/updated successfully");
            return "Admin-Product";

        } catch (Exception e) {
            model.addAttribute("products", productService.adminWarehouseProducts(userDto.getId()));
            model.addAttribute("users", authService.getAllActiveUsers());
            model.addAttribute("success", false);
            model.addAttribute("message", e.getMessage());
            return "Admin-Product";
        }
    }

    @GetMapping("/admin/product-status-update")
    public String deleteProduct(@RequestParam(name = "productId") Long productId,
                                HttpServletRequest request,
                                HttpSession httpSession, HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";

        try {
            productService.productStatusUpdate(productId);
            model.addAttribute("products", productService.adminWarehouseProducts(userDto.getId()));
            model.addAttribute("users", authService.getAllActiveUsers());
            model.addAttribute("success", true);
            model.addAttribute("message", "Product status updated successfully");
            return "Admin-Product";

        } catch (Exception e) {
            model.addAttribute("products", productService.adminWarehouseProducts(userDto.getId()));
            model.addAttribute("users", authService.getAllActiveUsers());
            model.addAttribute("success", false);
            model.addAttribute("message", e.getMessage());
            return "Admin-Product";
        }
    }
    

    /* Order Related functions */
    
    @GetMapping("/admin/get-orders")
    public String getActiveOrders(@RequestParam(required = false) String tid, @RequestParam(required = false) String status,
            HttpServletRequest request, HttpSession httpSession, HttpServletResponse response, Model model) {
        
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";

        model.addAttribute("orders", productService.getAdminOrders(userDto.getWarehouseId(), tid, status));
        return "Admin-Orders";
    }

    @PostMapping("/admin-update-order-status")
    public String updateOrderStatus(@ModelAttribute(name = "UpdateOrderStatusForm") UpdateOrderStatusForm updateOrderStatusForm,
                                    HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";

        try {
            productService.updateOrderStatus(updateOrderStatusForm);
            return "redirect:/admin/get-active-orders";

        } catch (Exception e) {
            return "redirect:/admin/get-active-orders";
        }
    }
    
    
    /* Chat Related functions */

    @GetMapping("/admin/chat")
    public String getChatPage(HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";
        model.addAttribute("user", userDto);
        return "Admin-chat-room";
    }
    
    /* Users Related functions */
    
    @GetMapping("/admin/get-all-users")
    public String getAllUsers(HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";
        model.addAttribute("users", authService.getAllUsers());
        return "Users";
    }
    
    @PostMapping("/admin/users/active/{id}")
    public String enableOrDisableUser(@PathVariable Long id, HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";
        authService.toggleUserActiveStatus(id);
        return "redirect:/admin/get-all-users";
    }

}