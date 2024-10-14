package com.example.inventorySystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.inventorySystem.entity.User;
import com.example.inventorySystem.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByUser(User user);
    List<Product> findByUserAndProductNameContainingIgnoreCase(User user, String productName);
    List<Product> findByWarehouseId(Long warehouseId);
    List<Product> findByWarehouseIdAndProductNameContainingIgnoreCase(Long warehouseId, String productName);
    
    @Query("SELECT DISTINCT p.warehouseId FROM Product p WHERE p.user.id = :userId")
    List<Long> findDistinctWarehouseIdsByUserId(@Param("userId") Long userId);
    
    List<Product> findByWarehouseIdAndUserId(Long warehouseId, Long userId);
}
