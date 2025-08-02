package com.develead.smile.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customer")
@Getter @Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customer_id;

    @OneToOne
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    private String email;
    private String address;

    @Column(nullable = false, length = 1)
    private String deleted = "N";
}

