package com.patika.akbankservice.controller;

import com.patika.akbankservice.model.CreditCard;
import com.patika.akbankservice.producer.NotificationProducer;
import com.patika.akbankservice.service.CreditCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/akbank/credit-cards")
@RequiredArgsConstructor
public class CreditCardController {

    private final CreditCardService creditCardService;

    private final NotificationProducer notificationProducer;

    @PostMapping("/{email}")
    public CreditCard createCreditCardApplication(@PathVariable String email, @RequestBody CreditCard creditCard) {
        return creditCardService.createCreditCardApplicationByEmail(email, creditCard);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CreditCard>> getAll() {
        return ResponseEntity.ok(creditCardService.getAllCreditCardApplications());
    }

    @GetMapping("/{email}")
    public ResponseEntity<List<CreditCard>> getCreditCardApplicationsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(creditCardService.getCreditCardApplicationsByEmail(email));
    }


}
