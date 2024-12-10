package com.example.inventorySystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.trackingNumber LIKE %:trackingNumber%")
    List<Order> findByUserAndTrackingNumberContaining(@Param("user") User user, @Param("trackingNumber") String trackingNumber);

    @Query("SELECT SUM(o.quantity) FROM Order o WHERE o.product.id = :productId AND o.orderStatus = :orderStatus")
    Integer sumQuantityByProductAndStatus(@Param("productId") Long productId, @Param("orderStatus") String orderStatus);
    
    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.orderStatus = :orderStatus AND o.trackingNumber LIKE %:trackingNumber%")
    List<Order> findByUserAndOrderStatusAndTrackingNumberContaining(
            @Param("user") User user,
            @Param("orderStatus") String orderStatus,
            @Param("trackingNumber") String trackingNumber);
    
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
