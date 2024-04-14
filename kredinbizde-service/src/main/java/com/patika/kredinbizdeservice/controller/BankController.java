package com.patika.kredinbizdeservice.controller;

import com.patika.kredinbizdeservice.exceptions.ExceptionMessages;
import com.patika.kredinbizdeservice.exceptions.KredinbizdeException;
import com.patika.kredinbizdeservice.model.Bank;
import com.patika.kredinbizdeservice.model.User;
import com.patika.kredinbizdeservice.service.BankService;
import com.patika.kredinbizdeservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/kredinbizde/banks")
@RequiredArgsConstructor
public class BankController {

    /* @Autowired*/
    private final BankService bankService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Bank create(@RequestBody Bank bank) {
        //System.out.println("bankService: " + bankService.hashCode());
        return bankService.save(bank);
    }

    @GetMapping("/{bankID}")
    public List<User> getAll(@PathVariable Long bankID) {
        return bankService.getAllCustomers(bankID);


    }

    @GetMapping("/{bankID}/{userID}")
    public User getByEmail(@PathVariable Long bankID, @PathVariable Long userID) {
        return bankService.findCustomerBanks(bankID, userID);
    }


    @PostMapping("/{bankID}/{email}")
    public Bank addCustomer(@PathVariable Long bankID, @PathVariable String email) {
        return bankService.addCustomer(bankID, email);
    }

    @DeleteMapping("/{bankID}/{email}")
    public Bank deleteCustomer(@PathVariable Long bankID, @PathVariable String email) {
        return bankService.deleteCustomer(bankID, email);
    }


}
