package com.example.inventorySystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Base64;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String trackingNumber;
    private Integer quantity;
    private String orderStatus;
    private Long warehouseId;
    
    @Column(name = "order_date")
    private Timestamp orderDate;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "LONGBLOB")
    private byte[] shippingLabel;
    
    public String generateBase64ShippingLabel() {
        return Base64.getEncoder().encodeToString(this.shippingLabel);
    }
}
