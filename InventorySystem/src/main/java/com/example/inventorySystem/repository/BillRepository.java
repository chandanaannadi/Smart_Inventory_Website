package com.example.inventorySystem.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.inventorySystem.entity.Bill;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
	Bill findTopByUserIdAndPaidFalseOrderByDueDateAsc(Long userId);
	List<Bill> findAllByUserIdOrderByWarehouseIdAscBillStartDateDesc(Long userId);
	Bill findTopByUserIdAndWarehouseIdOrderByBillEndDateDesc(Long userId, Long warehouseId);
	List<Bill> findAllByUserIdAndBillStartDateBetween(Long userId, Timestamp startOfMonth, Timestamp endOfMonth);
}

