package com.example.inventorySystem.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import com.example.inventorySystem.dto.MonthlySaleDto;
import com.example.inventorySystem.dto.UserDto;
import com.example.inventorySystem.dto.forms.*;
import com.example.inventorySystem.dto.helper.CommonUtils;
import com.example.inventorySystem.dto.helper.DateUtils;
import com.example.inventorySystem.entity.*;
import com.example.inventorySystem.repository.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @SuppressWarnings("deprecation")
	@SneakyThrows
    public List<Product> userProducts(ProductSearchForm productSearchForm, Long userId) {
    	User user = userRepository.getById(userId);
        if (StringUtils.isEmpty(productSearchForm.getProductName())) {
            return productRepository.findByUser(user).stream().filter(Product::getActive).collect(Collectors.toList());
        }
        return productRepository.findByUserAndProductNameContainingIgnoreCase(user, productSearchForm.getProductName()).stream().filter(Product::getActive).collect(Collectors.toList());
    }

    @SuppressWarnings("deprecation")
	@SneakyThrows
    public List<Product> adminWarehouseProducts(ProductSearchForm productSearchForm, Long userId) {
    	User user = userRepository.getById(userId);
        if (StringUtils.isEmpty(productSearchForm.getProductName())) {
            return productRepository.findByWarehouseId(user.getWarehouseId());
        }
        return productRepository.findByWarehouseIdAndProductNameContainingIgnoreCase(user.getWarehouseId(), productSearchForm.getProductName());
    }

    @SneakyThrows
    public void createOrder(OrderForm orderForm, MultipartFile file, Long userId) {

    	User user = userRepository.getById(userId);
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
        } else {
        	throw new Exception("Given product quantity is not valid.");
        }
    }

    @SneakyThrows
    public List<Order> getOrders(Long userId) {
        return orderRepository.findByUser(userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Invalid user")));
    }
    
    @SneakyThrows
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new Exception("Invalid ID"));
    }
    
    public List<Order> getActiveOrders(Long warehouseId) {
        List<Order> orders = orderRepository.findByWarehouseIdAndOrderStatusNot(warehouseId, "DELIVERED");

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
        
        return productRepository.save(product);
    }

    public void productStatusUpdate(Long productId) {
        Product product = productRepository.findById(productId).get();
        product.setActive(!product.getActive());
        productRepository.save(product);
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

}
