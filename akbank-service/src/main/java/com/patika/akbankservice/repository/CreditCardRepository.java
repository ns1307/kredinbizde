package com.patika.akbankservice.repository;

import com.patika.akbankservice.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    List<CreditCard> findCreditCardsByUserID(String userID);
}
