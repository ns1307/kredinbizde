package com.patika.akbankservice.controller;

import com.patika.akbankservice.model.Loan;
import com.patika.akbankservice.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/akbank/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;



    @PostMapping("/{email}")
    public Loan createLoanApplication(@PathVariable String email, @RequestBody Loan loan) {
        return loanService.createLoanApplicationByEmail(email, loan);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Loan>> getAll() {
        return ResponseEntity.ok(loanService.getAllLoanApplications());
    }

    @GetMapping("/{email}")
    public ResponseEntity<List<Loan>> getLoanApplicationsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(loanService.getLoanApplicationsByEmail(email));
    }




}
