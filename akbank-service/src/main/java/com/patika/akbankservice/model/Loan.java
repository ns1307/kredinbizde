package com.patika.akbankservice.model;

import com.patika.akbankservice.enums.ApplicationStatus;
import com.patika.akbankservice.enums.LoanType;
import com.patika.akbankservice.model.constants.ApplicationEntityColumnConstants;
import com.patika.akbankservice.model.constants.LoanEntityColumnConstants;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loans")
public class Loan extends Application{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @Column(name = LoanEntityColumnConstants.AMOUNT)
    private BigDecimal amount;

    @Column(name = LoanEntityColumnConstants.INTEREST_RATE )
    private BigDecimal interestRate;

    @Column(name = ApplicationEntityColumnConstants.CREATE_DATE)
    private LocalDate createdDate;

    @Column(name = ApplicationEntityColumnConstants.UPDATED_DATE)
    private LocalDate updatedDate;

    @Column(name = ApplicationEntityColumnConstants.USER_ID)
    private String userID;

    @Enumerated(EnumType.STRING)
    @Column(name = ApplicationEntityColumnConstants.APPLICATION_STATUS)
    private ApplicationStatus applicationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = LoanEntityColumnConstants.LOAN_TYPE)
    private LoanType loanType;

}
