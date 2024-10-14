package com.example.inventorySystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.inventorySystem.entity.Order;
import com.example.inventorySystem.entity.User;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByWarehouseId(Long warehouseId);
    List<Order> findByWarehouseIdAndOrderStatus(Long warehouseId, String orderStatus);
    List<Order> findByWarehouseIdAndTrackingNumber(Long warehouseId, String trackingNumber);
    List<Order> findByWarehouseIdAndOrderStatusAndTrackingNumber(Long warehouseId, String orderStatus, String trackingNumber);
    List<Order> findByUser(User user);
    List<Order> findByUserAndOrderStatus(User user, String orderStatus);
    List<Order> findByUserAndTrackingNumber(User user, String trackingNumber);
    List<Order> findByUserAndOrderStatusAndTrackingNumber(User user, String orderStatus, String trackingNumber);

    @Query(value = "SELECT " +
            "MONTH(order_date) AS month, " +
            "COUNT(*) AS numberOfOrders " +
            "FROM " +
            "orders " +
            "WHERE " +
            "YEAR(order_date) = YEAR(CURDATE()) " +
            "GROUP BY " +
            "MONTH(order_date) ", nativeQuery = true)
    List<Object[]> getMonthlyOrdersData();
}
