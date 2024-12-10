package com.example.inventorySystem.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.inventorySystem.entity.Bill;
import com.example.inventorySystem.entity.User;
import com.example.inventorySystem.repository.BillRepository;
import com.example.inventorySystem.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@Service
@AllArgsConstructor
public class BillService {

    private final BillRepository billRepository;

    private final UserRepository userRepository;

    public Bill getMostRecentBill(Long userId, Long warehouseId) {
        // Retrieve the most recent bill for the user and warehouse
        return billRepository.findTopByUserIdAndWarehouseIdOrderByBillEndDateDesc(userId, warehouseId);
    }

    public Bill createNewMonthlyBill(Long userId, Long warehouseId, Double amountDue, LocalDate endOfMonth) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        LocalDateTime startDate = endOfMonth.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDate = endOfMonth.atTime(23, 59, 59);

        Bill newBill = Bill.builder()
                .user(user)
                .warehouseId(warehouseId)
                .billStartDate(Timestamp.valueOf(startDate))
                .billEndDate(Timestamp.valueOf(endDate))
                .dueDate(Timestamp.valueOf(endDate.plusDays(10)))
                .amountDue(amountDue)
                .paid(false)
                .subscriptionActive(true)
                .build();

        return billRepository.save(newBill);
    }

    public List<Bill> getCurrentMonthBills(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).with(LocalTime.MIN);
        LocalDateTime endOfMonth = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).with(LocalTime.MAX);

        return billRepository.findAllByUserIdAndBillStartDateBetween(
                userId,
                Timestamp.valueOf(startOfMonth),
                Timestamp.valueOf(endOfMonth)
        );
    }

    // Get the billing history for a user
    public List<Bill> getBillingHistoryForUser(Long userId) {
        return billRepository.findAllByUserIdOrderByWarehouseIdAscBillStartDateDesc(userId);
    }

    // Save the bill after updating its status
    public void saveBill(Bill bill) {
        billRepository.save(bill);
    }
    
    // Get an unpaid bill for a user
    @SneakyThrows
    public Bill getBillById(Long billId) {
        return billRepository.findById(billId)
                .orElseThrow(() -> new Exception("Invalid order id"));
    }
}
