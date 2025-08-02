package com.develead.smile.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "clinic") @Getter @Setter
public class Clinic {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer clinic_id;
    @Column(nullable = false) private String clinicName;
    private String address;
    private String phoneNumber;
    @Column(nullable = false) private boolean isActive = true;
}