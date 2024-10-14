package com.example.inventorySystem.entity;

import java.sql.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bill")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_start_date", nullable = false)
    private Timestamp billStartDate;
    
    @Column(name = "bill_end_date", nullable = false)
    private Timestamp billEndDate;
    
    @Column(name = "due_date", nullable = false)
    private Timestamp dueDate;
    
    private Boolean paid;

    private Long warehouseId;
    
    @Column(name = "payment_date")
    private Timestamp paymentDate;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "amount_due", nullable = false)
    private Double amountDue;  // Add amount field

    @Column(name = "subscription_active", nullable = false)
    private Boolean subscriptionActive;
}
