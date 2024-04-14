package com.patika.akbankservice.model;

import com.patika.akbankservice.enums.ApplicationStatus;
import com.patika.akbankservice.enums.CardType;
import com.patika.akbankservice.model.constants.ApplicationEntityColumnConstants;
import com.patika.akbankservice.model.constants.CreditCardEntityConstants;
import com.patika.akbankservice.model.constants.LoanEntityColumnConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "credit-cards")
public class CreditCard extends Application{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long creditCardApplicationId;

    @Column(name = CreditCardEntityConstants.CARD_LIMIT)
    private BigDecimal cardLimit;

    @Column(name = CreditCardEntityConstants.CARD_NUMBER )
    private String cardNumber;

    @Column(name= CreditCardEntityConstants.CVV)
    private String cvv;

    @Column(name= CreditCardEntityConstants.EXPIRY_DATE)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = LoanEntityColumnConstants.LOAN_TYPE)
    private CardType cardType;

    @Column(name = ApplicationEntityColumnConstants.CREATE_DATE)
    private LocalDate createdDate;

    @Column(name = ApplicationEntityColumnConstants.UPDATED_DATE)
    private LocalDate updatedDate;

    @Column(name = ApplicationEntityColumnConstants.USER_ID)
    private String userID;

    @Enumerated(EnumType.STRING)
    @Column(name = ApplicationEntityColumnConstants.APPLICATION_STATUS)
    private ApplicationStatus applicationStatus;


}
