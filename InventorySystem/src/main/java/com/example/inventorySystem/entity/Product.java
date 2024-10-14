package com.example.inventorySystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name")
    private String productName;
    
    private String upc;
    private String trackingId;
    private String packageId;
    private Integer quantity;
    private Long warehouseId;
    private Boolean availability;
    
    @Column(name = "arival_date")
    private Timestamp arivalDate;

    @Column(name = "is_active")
    private Boolean active;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductHistory> productHistory;

    public String generateBase64Image() {
        return Base64.encodeBase64String(this.image);
    }

}
