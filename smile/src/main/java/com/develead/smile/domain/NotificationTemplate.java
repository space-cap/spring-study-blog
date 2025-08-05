package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "notification_template") @Getter @Setter
public class NotificationTemplate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer template_id;
    @Column(unique = true, nullable = false) private String templateKey;
    private String description;
    @Column(nullable = false) private String channel;
    private String subject;
    @Lob @Column(nullable = false) private String body;
    @Column(nullable = false) private char deleted = 'N';
    private Integer createdBy;
    private Integer updatedBy;
}
