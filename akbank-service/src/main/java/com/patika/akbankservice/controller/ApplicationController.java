package com.patika.akbankservice.controller;

import com.patika.akbankservice.model.Bank;
import com.patika.akbankservice.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/akbank/customers")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/add/{email}")
    public Bank addCustomer(@PathVariable String email) {
        return applicationService.addCustomerByEmail(email);
    }

    @DeleteMapping("/delete/{email}")
    public Bank removeCustomer(@PathVariable String email) {
        return applicationService.removeCustomer(email);
    }

}
