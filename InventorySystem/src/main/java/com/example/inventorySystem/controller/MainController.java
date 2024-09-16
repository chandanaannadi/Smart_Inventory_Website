package com.example.inventorySystem.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.inventorySystem.dto.UserDto;
import com.example.inventorySystem.dto.forms.*;
import com.example.inventorySystem.entity.Order;
import com.example.inventorySystem.service.AuthService;
import com.example.inventorySystem.service.ProductService;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    AuthService authService;
    @Autowired
    ProductService productService;

    /* Login/SignUp/Logout/Reset Related functions */

    @GetMapping("/signUp")
    public String signUpPage() {
        return "Signup";
    }

    @PostMapping("/signUp")
    public String signUp(@ModelAttribute(name = "SignUpForm") SignUpForm signUpForm, HttpSession httpSession,
                         HttpServletResponse response, Model model) {
        try {
            authService.signUp(signUpForm);
            return "Login";

        } catch (Exception e) {
            model.addAttribute("error", true);
            model.addAttribute("message", e.getMessage());
            return "Signup";
        }
    }


    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, HttpSession session, Model model) {
        UserDto userDto = authService.authenticateUser(request, session);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        if (userDto != null) {
            return "redirect:/";
        }
        return "Login";
    }
    
    @PostMapping("/login")
    public String login(@ModelAttribute(name = "LoginForm") LoginForm loginForm, HttpSession httpSession,
                        HttpServletResponse response, Model model) {
        try {
            UserDto userDto = authService.login(loginForm);

            if (loginForm.getRememberMe()) {
                Cookie rememberMeCookie = new Cookie("remember-me-cookie", userDto.getRememberMeToken());
                rememberMeCookie.setMaxAge(2628288); // 1 month in seconds
                response.addCookie(rememberMeCookie);
            } else {
                Cookie rememberMeCookie = new Cookie("remember-me-cookie", "");
                rememberMeCookie.setMaxAge(0); // 1 month in seconds
                response.addCookie(rememberMeCookie);
            }
            httpSession.setAttribute("session-token", userDto.getSessionToken());
            if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
                return "redirect:/admin/dashboard";
            return "redirect:/";

        } catch (Exception e) {
            if (e.getMessage().equals("Invalid Credentials")) {
                model.addAttribute("invalidCredentials", true);
                return "Login";
            }
        }
        return "Login";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(HttpServletRequest request, HttpSession session, Model model) {
        UserDto userDto = authService.authenticateUser(request, session);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        if (userDto != null) {
            return "redirect:/";
        }
        model.addAttribute("success", "");
        model.addAttribute("message", "");
        return "Forgot_password";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute(name = "UserProfileForm") UserProfileForm userProfileForm,
                                 HttpServletRequest request,
                                 HttpSession httpSession, HttpServletResponse response, Model model) {
        try {
            authService.changePassword(userProfileForm);
            model.addAttribute("success", true);
            model.addAttribute("message", "User password updated successfully, Please proceed to login");
            return "forgot-password";

        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("message", e.getMessage());
            return "forgot-password";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpSession httpSession, HttpServletResponse response) {
        Cookie rememberMeCookie = new Cookie("remember-me-cookie", "");
        rememberMeCookie.setMaxAge(0); // 1 month in seconds
        response.addCookie(rememberMeCookie);
        httpSession.removeAttribute("session-token");
        return "Login";
    }


    /* Products Related functions */
    
    @GetMapping("/")
    public String index(HttpServletRequest request, HttpSession httpSession, HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        model.addAttribute("orderPlaced",  "null");
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        if (userDto != null) {
            httpSession.setAttribute("session-token", userDto.getSessionToken());
            model.addAttribute("products", productService.userProducts(ProductSearchForm.builder().categoryId(0L).build(), userDto.getId()));
            return "Product";
        }
        return "Login";
    }

    @PostMapping("/product-search")
    public String searchProduct(@ModelAttribute(name = "ProductSearchForm") ProductSearchForm productSearchForm,
                                HttpSession httpSession,
                                HttpServletRequest request,
                                HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        
        model.addAttribute("orderPlaced",  "null");
        model.addAttribute("products", productService.userProducts(productSearchForm, userDto.getId()));
        return "Product";
    }
    
    
    /* User Profile Related functions */

    @GetMapping("/user-profile")
    public String userProfile(HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        model.addAttribute("user", userDto);
        return "User-profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute(name = "UserProfileForm") UserProfileForm userProfileForm,
                                @RequestParam("file") MultipartFile file,
                                HttpServletRequest request,
                                HttpSession httpSession, HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";

        try {
            UserDto user = authService.updateUserProfile(userProfileForm, file);
            model.addAttribute("user", user);
            model.addAttribute("success", true);
            model.addAttribute("message", "User profile updated successfully");
            return "User-profile";

        } catch (Exception e) {
            model.addAttribute("error", true);
            model.addAttribute("message", e.getMessage());
            model.addAttribute("user", userDto);
            return "User-profile";
        }
    }
    
    
    /* Order Related functions */

    @GetMapping("/get-orders")
    public String getOrders(HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        model.addAttribute("user", userDto);
        List<Order> orders = productService.getOrders(userDto.getId());
        model.addAttribute("orders", orders);
        return "My-Orders";
    }
    
	@PostMapping("/create-order")
	public String confirmOrder(@ModelAttribute(name = "CreateOrderForm") OrderForm orderForm,
			                   @RequestParam("file") MultipartFile file,
	                           HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
	    if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
	        return "redirect:/admin/dashboard";
	    productService.createOrder(orderForm, file, userDto.getId());
	    model.addAttribute("orderPlaced", true);
	    model.addAttribute("user", userDto);
	    model.addAttribute("products", productService.userProducts(ProductSearchForm.builder().categoryId(0L).build(), userDto.getId()));
        return "Product";
	}
    
    @PostMapping("/delete-order")
    @ResponseBody
    public Boolean deleteOrder(HttpServletRequest request, HttpSession httpSession,
                             Model model, @RequestBody DeleteOrderForm deleteOrderForm) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return false;
        productService.deleteOrder(userDto, deleteOrderForm);
        return true;
    }
    
    
    /* Chat Related functions */
    
    @GetMapping("/chat")
    public String getChatPage(HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        model.addAttribute("user", userDto);
        return "chat-room";
    }


}