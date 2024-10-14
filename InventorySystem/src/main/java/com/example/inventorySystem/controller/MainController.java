package com.example.inventorySystem.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import com.example.inventorySystem.dto.UserDto;
import com.example.inventorySystem.dto.forms.*;
import com.example.inventorySystem.entity.Bill;
import com.example.inventorySystem.entity.Order;
import com.example.inventorySystem.entity.OrderHistory;
import com.example.inventorySystem.entity.ProductHistory;
import com.example.inventorySystem.service.AuthService;
import com.example.inventorySystem.service.BillService;
import com.example.inventorySystem.service.ProductService;

import java.sql.Timestamp;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private AuthService authService;
    @Autowired
    private ProductService productService;
    @Autowired
    private BillService billService;
    
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
                rememberMeCookie.setMaxAge(0);
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
            model.addAttribute("products", productService.userProducts(userDto.getId()));
            return "Product";
        }
        return "Login";
    }
    
    @GetMapping("/products/{productId}/history")
    public String viewProductHistory(@PathVariable Long productId, HttpServletRequest request, HttpSession httpSession, Model model) {
    	UserDto userDto = authService.authenticateUser(request, httpSession);
    	
        List<ProductHistory> historyList = productService.getProductHistory(productId);
        model.addAttribute("productHistory", historyList);
        model.addAttribute("productId", productId);
        model.addAttribute("isAdmin", userDto.getRole().equalsIgnoreCase("admin"));
        return "product-history";
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
    public String getOrders(@RequestParam(required = false) String tid, @RequestParam(required = false) String status,
    		HttpServletRequest request, HttpSession httpSession, Model model) {
    	
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        
        model.addAttribute("user", userDto);
        model.addAttribute("orders", productService.getOrders(userDto.getId(), tid, status));
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
	    model.addAttribute("products", productService.userProducts(userDto.getId()));
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
    
    @GetMapping("/shippingLabel/{orderId}")
    public ResponseEntity<byte[]> downloadShippingLabel(@PathVariable Long orderId,
    		                  HttpServletRequest request, HttpSession httpSession) {
    	UserDto userDto = authService.authenticateUser(request, httpSession);
	    if (userDto == null) {
	    	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }
	    
        try {
	    	Order order = productService.getOrderById(orderId);
	
	        if (order == null || order.getShippingLabel() == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	        }
	
	        byte[] fileData = order.getShippingLabel();
	
	        HttpHeaders headers = new HttpHeaders();
	        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=shippingLabel.pdf");
	        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
	
	        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        } catch(Exception e) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/orders/{orderId}/history")
    public String viewOrderHistory(@PathVariable Long orderId, HttpServletRequest request, HttpSession httpSession, Model model) {
    	UserDto userDto = authService.authenticateUser(request, httpSession);
    	
        List<OrderHistory> historyList = productService.getOrderHistory(orderId);
        model.addAttribute("orderHistory", historyList);
        model.addAttribute("orderId", orderId);
        model.addAttribute("isAdmin", userDto.getRole().equalsIgnoreCase("admin"));
        return "order-history";
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
    
    /* Billing Related functions */
    
    @GetMapping("billing/history")
    public String getBillingHistory(HttpServletRequest request, HttpSession session, Model model) {
        UserDto userDto = authService.authenticateUser(request, session);
        if (userDto == null) {
            return "redirect:/login";
        }

        List<Long> warehouseIds = productService.getWarehouseIdsForUser(userDto.getId());

        // Create bills for each warehouse if no bills exist for the current month
        for (Long warehouseId : warehouseIds) {
            Bill currentBill = billService.getCurrentBillingCycle(userDto.getId(), warehouseId);
            if (currentBill == null) {
                double warehouseAmountDue = productService.calculateTotalForWarehouse(warehouseId, userDto.getId());
                billService.createNewMonthlyBill(userDto.getId(), warehouseId, warehouseAmountDue);
            }
        }

        List<Bill> currentMonthBills = billService.getCurrentMonthBills(userDto.getId());
        List<Bill> billingHistory = billService.getBillingHistoryForUser(userDto.getId());

        // Add data to the model
        model.addAttribute("currentMonthBills", currentMonthBills);
        model.addAttribute("billingHistory", billingHistory);

        return "billing-page";
    }

    @PostMapping("billing/pay")
    @ResponseBody
    public Boolean makePayment(HttpServletRequest request, HttpSession session,
            Model model, @RequestBody BillingForm billingForm) {
        UserDto userDto = authService.authenticateUser(request, session);
        if (userDto == null) {
            return false;
        }

        Bill unpaidBill = billService.getBillById(billingForm.getBillId());
        if (unpaidBill != null) {
            unpaidBill.setPaid(true);
            unpaidBill.setPaymentDate(new Timestamp(System.currentTimeMillis()));
            billService.saveBill(unpaidBill);

            return true;
        }

        return false;
    }

}