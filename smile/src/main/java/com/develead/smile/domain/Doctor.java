package com.develead.smile.domain;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "doctor") @Getter @Setter
public class Doctor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer doctor_id;
    @Column(nullable = false) private String name;
    private String specialty;
    @ManyToOne @JoinColumn(name = "clinic_id", nullable = false) private Clinic clinic;
}