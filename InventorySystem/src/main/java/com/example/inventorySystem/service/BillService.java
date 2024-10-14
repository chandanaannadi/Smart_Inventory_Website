package com.example.inventorySystem.service;

import java.sql.Timestamp;
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

    public Bill getCurrentBillingCycle(Long userId, Long warehouseId) {
        // Get the most recent bill for the user and warehouse
        Bill latestBill = billRepository.findTopByUserIdAndWarehouseIdOrderByBillEndDateDesc(userId, warehouseId);
        
        if (latestBill != null) {
            // Get the current month and year
            LocalDateTime now = LocalDateTime.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();
            
            // Get the month and year of the bill's end date
            LocalDateTime billEndDate = latestBill.getBillEndDate().toLocalDateTime();
            int billEndMonth = billEndDate.getMonthValue();
            int billEndYear = billEndDate.getYear();
            
            // Check if the bill is for the current month and year
            if (billEndMonth == currentMonth && billEndYear == currentYear) {
                return latestBill;
            }
        }
        
        return null;
    }

    public Bill createNewMonthlyBill(Long userId, Long warehouseId, Double amountDue) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        LocalDateTime now = LocalDateTime.now();

        // Set billing cycle to follow the calendar month
        LocalDateTime billStartDate = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime billEndDate = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).toLocalDate().atTime(23, 59, 59);

        Bill lastBill = billRepository.findTopByUserIdAndWarehouseIdOrderByBillEndDateDesc(userId, warehouseId);

        // Ensure that we don't create overlapping bills for the same month
        if (lastBill != null && lastBill.getBillEndDate().toLocalDateTime().getMonth() == now.getMonth()) {
            throw new RuntimeException("Bill for the current month already exists");
        }

        Bill newBill = Bill.builder()
                .user(user)
                .warehouseId(warehouseId)
                .billStartDate(Timestamp.valueOf(billStartDate))
                .billEndDate(Timestamp.valueOf(billEndDate))
                .dueDate(Timestamp.valueOf(billEndDate.plusDays(10)))
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
