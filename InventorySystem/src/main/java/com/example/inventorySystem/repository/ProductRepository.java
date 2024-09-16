package com.example.inventorySystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
