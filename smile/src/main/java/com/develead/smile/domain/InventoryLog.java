package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "inventory_log") @Getter @Setter
public class InventoryLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer log_id;
    @ManyToOne @JoinColumn(name = "item_id", nullable = false) private InventoryItem item;
    @Column(nullable = false) private String changeType;
    @Column(nullable = false) private int quantityChanged;
    private String reason;
    private Integer relatedRecordId;
    @Column(nullable = false) private LocalDateTime logDate = LocalDateTime.now();
    private Integer loggedBy;
}
