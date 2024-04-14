package com.patika.kredinbizdeservice.model;

import com.patika.kredinbizdeservice.model.constant.BankEntityColumnConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "banks")
public class Bank implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = BankEntityColumnConstants.NAME, unique = false, nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = BankEntityColumnConstants.ADDRESS, nullable = true, unique = false)
    private Address address;


    @Column(name = BankEntityColumnConstants.PHONE_NUMBER, unique = false, nullable = false)
    private String phone;


    //bankaları ve müşterilerini içeren tablodur
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinTable(name = "bank_customers", joinColumns = @JoinColumn(name = "bank_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> customers = new ArrayList<>();

    @Override
    public String toString() {
        return "Bank{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ",address=" + getAddress().toString() +
                '}';
    }
}
