package com.example.inventorySystem.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.inventorySystem.dto.MonthlySaleDto;
import com.example.inventorySystem.dto.UserDto;
import com.example.inventorySystem.dto.forms.*;
import com.example.inventorySystem.dto.helper.CommonUtils;
import com.example.inventorySystem.dto.helper.DateUtils;
import com.example.inventorySystem.entity.*;
import com.example.inventorySystem.repository.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ProductHistoryRepository productHistoryRepository;

    @SuppressWarnings("deprecation")
	@SneakyThrows
    public List<Product> userProducts(Long userId) {
    	User user = userRepository.getById(userId);
        return productRepository.findByUser(user).stream().filter(Product::getActive).collect(Collectors.toList());
    }

    @SuppressWarnings("deprecation")
	@SneakyThrows
    public List<Product> adminWarehouseProducts(Long userId) {
    	User user = userRepository.getById(userId);
        
    	return productRepository.findByWarehouseId(user.getWarehouseId());
    }

    @SneakyThrows
    public void createOrder(OrderForm orderForm, MultipartFile file, Long userId) {

    	User user = userRepository.findById(userId).orElseThrow(() -> new Exception("Invalid User"));
        Product product = productRepository.findById(orderForm.getProductId()).orElseThrow(() -> new Exception("No data found"));
        if(product.getQuantity() >= orderForm.getQuantity()) {
        	Order order = Order.builder().product(product)
            		.trackingNumber(CommonUtils.generateRandomTrackingId(14))
            		.quantity(orderForm.getQuantity())
            		.orderStatus("PROCESSING")
            		.warehouseId(orderForm.getWarehouseId())
            		.orderDate(DateUtils.convertStringToTimestamp(orderForm.getOrderDate()))
            		.user(user)
            		.build();
            
            if (!file.isEmpty())
            	order.setShippingLabel(file.getBytes());
            else
            	throw new Exception("Please upload shipping label.");
            
            orderRepository.save(order);
            
            product.setQuantity(product.getQuantity() - order.getQuantity());
            productRepository.save(product);
            
            recordOrderHistory(order, "Created", "Order has been created.");
            recordProductHistory(product, "Product Check Out", order.getQuantity() + " Product(s) checked out from Warehouse " + product.getWarehouseId() + ". Remaining Quantity: "+ product.getQuantity());
        } else {
        	throw new Exception("Given product quantity is not valid.");
        }
    }

    @SneakyThrows
    public List<Order> getOrders(Long userId, String tid, String status) {
        List<Order> orders = null;
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception("Invalid user"));
        
        if (tid != null && !tid.isBlank() && status != null && !status.isBlank()) {
            orders = orderRepository.findByUserAndOrderStatusAndTrackingNumber(user, status, tid);
        } else if (tid != null && !tid.isBlank()) {
            orders = orderRepository.findByUserAndTrackingNumber(user, tid);
        } else if (status != null && !status.isBlank()) {
            orders = orderRepository.findByUserAndOrderStatus(user, status);
        } else {
            orders = orderRepository.findByUser(user);
        }

        return orders;
    }
    
    @SneakyThrows
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new Exception("Invalid ID"));
    }
    
    public List<Order> getAdminOrders(Long warehouseId, String tid, String status) {
        List<Order> orders = null;

        if (tid != null && !tid.isBlank() && status != null && !status.isBlank()) {
            orders = orderRepository.findByWarehouseIdAndOrderStatusAndTrackingNumber(warehouseId, status, tid);
        } else if (tid != null && !tid.isBlank()) {
            orders = orderRepository.findByWarehouseIdAndTrackingNumber(warehouseId, tid);
        } else if (status != null && !status.isBlank()) {
            orders = orderRepository.findByWarehouseIdAndOrderStatus(warehouseId, status);
        } else {
            orders = orderRepository.findByWarehouseId(warehouseId);
        }

        return orders;
    }

    @SuppressWarnings("deprecation")
	@SneakyThrows
    public Product addProduct(ProductForm productForm, MultipartFile file, Long warehouseId) throws IOException {
        Product product =  productForm.getId() == null ? new Product() : productRepository.getById(productForm.getId());

        product.setProductName(productForm.getName());
        product.setUpc(productForm.getUpc());
        product.setQuantity(productForm.getQuantity());
        product.setArivalDate(DateUtils.convertStringToTimestamp(productForm.getArivalDate()));
        product.setAvailability(productForm.getAvailability() != null ? true : false);
        product.setTrackingId(productForm.getTrackingId());
        product.setPackageId(productForm.getPackageId());
        product.setUser(userRepository.getById(productForm.getUserId()));
        product.setWarehouseId(warehouseId);
        product.setActive(true);

        if (!file.isEmpty())
            product.setImage(file.getBytes());
        else if(productForm.getId() == null)
        	throw new Exception("Please select image");
        
        productRepository.save(product);
        
        if(productForm.getId() != null) {
        	recordProductHistory(product, "Product Details Updated", "Product Details were updated.");
        } else {
        	recordProductHistory(product, "Product Check In", "Product checked into Warehouse: " + product.getWarehouseId());
        }
        
        return product;
    }

    public void productStatusUpdate(Long productId) {
        Product product = productRepository.findById(productId).get();
        product.setActive(!product.getActive());
        productRepository.save(product);
        
        recordProductHistory(product, "Status Updated", "Product Active Status: " + product.getActive());
    }

    public List<MonthlySaleDto> getMonthlyOrders() {
        List<Object[]> resultList = orderRepository.getMonthlyOrdersData();
        List<MonthlySaleDto> monthlySales = new ArrayList<>();

        for (Object[] result : resultList) {
            int month = (int) result[0];
            Long numberOfOrders = (Long) result[1];
            monthlySales.add(new MonthlySaleDto(month, numberOfOrders));
        }

        return monthlySales;
    }

    @SneakyThrows
    public void updateOrderStatus(UpdateOrderStatusForm updateOrderStatusForm) {
        Order o = orderRepository.findById(updateOrderStatusForm.getOrderId())
                .orElseThrow(() -> new Exception("Invalid order id"));
        o.setOrderStatus(updateOrderStatusForm.getOrderStatus());
        orderRepository.save(o);
        
        // Record the status update in history
        recordOrderHistory(o, "Status Updated", "Order status changed to: " + o.getOrderStatus());
    }
    
    @SneakyThrows
    public void deleteOrder(UserDto userDto, DeleteOrderForm deleteOrderForm){
        Order o = orderRepository.findById(deleteOrderForm.getOrderId())
                .orElseThrow(() -> new Exception("Invalid order id"));
        
        Product product = o.getProduct();
        product.setQuantity(product.getQuantity()+o.getQuantity());
        productRepository.save(product);
        
        orderRepository.delete(o);
    }
    
    public void recordOrderHistory(Order order, String action, String details) {
        OrderHistory orderHistory = OrderHistory.builder()
                .order(order)
                .action(action)
                .details(details)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();
        orderHistoryRepository.save(orderHistory);
    }
    
    public void recordProductHistory(Product product, String action, String details) {
        ProductHistory productHistory = ProductHistory.builder()
                .product(product)
                .action(action)
                .details(details)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();
        productHistoryRepository.save(productHistory);
    }

    public List<OrderHistory> getOrderHistory(Long orderId) {
        return orderHistoryRepository.findByOrderId(orderId);
    }
    
    public List<Long> getWarehouseIdsForUser(Long userId) {
        return productRepository.findDistinctWarehouseIdsByUserId(userId);
    }
    
    public double calculateTotalForWarehouse(Long warehouseId, Long userId) {
        List<Product> products = productRepository.findByWarehouseIdAndUserId(warehouseId, userId);
        double total = 0.0;
        for (Product product : products) {
            total += product.getQuantity() * 1;
        }
        return total;
    }
    
    public List<ProductHistory> getProductHistory(Long productId) {
        return productHistoryRepository.findByProductId(productId);
    }

}
