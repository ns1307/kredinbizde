package com.patika.akbankservice.service;

import com.patika.akbankservice.exceptions.akbankServiceException;
import com.patika.akbankservice.model.Bank;
import com.patika.akbankservice.model.User;
import com.patika.akbankservice.service.constants.BankConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Scope(value = "singleton")
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    public Bank addCustomerByEmail(String email) {
        return WebClient.builder().baseUrl("http://localhost:8084").build().post() // GET isteği
                .uri("/api/kredinbizde/banks/"+ BankConstants.bankID+"/"+email) // URI ve path variable
                .retrieve()
                .onStatus(status -> status.value() == 404, response ->
                        response.bodyToMono(akbankServiceException.class)
                                .flatMap(errorResponse ->
                                        Mono.error(new akbankServiceException("No user found with this email."))))
                .bodyToMono(Bank.class).block();
    }


    public Bank removeCustomer(String email) {
        return WebClient.builder().baseUrl("http://localhost:8084").build().delete() // GET isteği
                .uri("/api/kredinbizde/banks/"+BankConstants.bankID+"/"+email) // URI ve path variable
                .retrieve()
                .onStatus(status -> status.value() == 404, response ->
                        response.bodyToMono(akbankServiceException.class)
                                .flatMap(errorResponse ->
                                        Mono.error(new akbankServiceException("Customer doesn't exist for this bank."))))
                .bodyToMono(Bank.class).block();
    }


    public User getUserByEmail(String email) {
        return WebClient.builder().baseUrl("http://localhost:8084").build().get()
                .uri("/api/kredinbizde/users/"+email) // URI ve path variable
                .retrieve()
                .onStatus(status -> status.value() == 404, response ->
                        response.bodyToMono(akbankServiceException.class)
                                .flatMap(errorResponse ->
                                        Mono.error(new akbankServiceException("No user found with this email."))))
                .bodyToMono(User.class).block();
    }

    public User isUserCustomer(String userID) {//check if user is customer of this bank
        return WebClient.builder().baseUrl("http://localhost:8084").build().get()
                .uri("/api/kredinbizde/banks/"+ BankConstants.bankID +"/"+userID)
                .retrieve()
                .onStatus(status -> status.value() == 404, response ->
                        response.bodyToMono(akbankServiceException.class)
                                .flatMap(errorResponse ->
                                        Mono.error(new akbankServiceException("This user is not customer of this bank.(Otherwise check bankID)"))))

                .bodyToMono(User.class).block();
    }

}
