package com.patika.akbankservice.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class User  {


    private Long id;

    private String name;

    private String surname;

    private LocalDate birthDate;

    private String email;

    private String password;

    private String phoneNumber;

    private Boolean isActive;

    private Address address;

}
