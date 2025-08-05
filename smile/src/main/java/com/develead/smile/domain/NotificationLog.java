package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "notification_log") @Getter @Setter
public class NotificationLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer log_id;
    @ManyToOne @JoinColumn(name = "customer_id") private Customer customer;
    @Column(nullable = false) private String channel;
    @Column(nullable = false) private String templateKey;
    @Lob @Column(nullable = false) private String content;
    @Column(nullable = false) private String status;
    @Column(nullable = false) private LocalDateTime sentAt = LocalDateTime.now();
}
