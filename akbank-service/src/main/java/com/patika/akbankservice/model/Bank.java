package com.patika.akbankservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Bank  {

    private Long id;

    private String name;

    private Address address;


    private String phone;


    private List<User> customers = new ArrayList<>();

}
