package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "inventory_item") @Getter @Setter
public class InventoryItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer item_id;
    @ManyToOne @JoinColumn(name = "clinic_id", nullable = false) private Clinic clinic;
    @Column(nullable = false) private String itemCode;
    @Column(nullable = false) private String itemName;
    @Lob private String description;
    @Column(nullable = false) private int quantity = 0;
    @Column(nullable = false) private String unit = "EA";
    private String supplier;
    @Column(nullable = false) private int safeStockLevel = 0;
    private Integer createdBy;
    private Integer updatedBy;
}
