package com.patika.akbankservice.repository;

import com.patika.akbankservice.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findLoansByUserID(String userID);


}
