package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "appointment_change_log") @Getter @Setter
public class AppointmentChangeLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer log_id;
    @ManyToOne @JoinColumn(name = "appointment_id", nullable = false) private Appointment appointment;
    @Column(nullable = false) private String fieldName;
    private String previousValue;
    @Column(nullable = false) private String newValue;
    @Column(nullable = false) private LocalDateTime changedAt = LocalDateTime.now();
    @ManyToOne @JoinColumn(name = "changed_by") private UserAccount changedBy;
}