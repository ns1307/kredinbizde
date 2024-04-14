package com.patika.akbankservice.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Address {

    private Long id;

    private String addressTitle;

    private String addressDescription;

    private String province;

}
