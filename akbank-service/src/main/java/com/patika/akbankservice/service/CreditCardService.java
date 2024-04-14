package com.patika.akbankservice.service;

import com.patika.akbankservice.enums.ApplicationStatus;
import com.patika.akbankservice.model.CreditCard;
import com.patika.akbankservice.model.User;
import com.patika.akbankservice.producer.NotificationProducer;
import com.patika.akbankservice.repository.CreditCardRepository;
import com.patika.akbankservice.service.constants.BankConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.patika.akbankservice.producer.enums.LogType;
import com.patika.akbankservice.producer.enums.SuccessType;

import java.time.LocalDate;
import java.util.List;

@Service
@Scope(value = "singleton")
@RequiredArgsConstructor
@Slf4j
public class CreditCardService extends ApplicationService {

    private final CreditCardRepository creditCardRepository;

    private final NotificationProducer notificationProducer;

    public List<CreditCard> getAllCreditCardApplications() {
        notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.READ, SuccessType.SUCCESS, "credit-cards", "Read all credit-card applications for bankID: "+BankConstants.bankID));
        return creditCardRepository.findAll();
    }

    public CreditCard createCreditCardApplication(CreditCard creditCard, User user) {
        creditCard.setCreatedDate(LocalDate.now());
        creditCard.setUpdatedDate(LocalDate.now());
        creditCard.setApplicationStatus(ApplicationStatus.INITIAL);
        creditCard.setUserID(String.valueOf(user.getId()));
        isUserCustomer(creditCard.getUserID());
        notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.CREATE, SuccessType.SUCCESS, "credit-cards", "New credit card application for user with userID:" +user.getId()+" for bankID:"+ BankConstants.bankID));
        return creditCardRepository.save(creditCard);
    }

    public List<CreditCard> getCreditCardApplications(String userID) {
        notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.READ, SuccessType.SUCCESS, "credit-cards", "Read all credit-card applications of user with userID:" +userID+" for bankID:"+ BankConstants.bankID));
        return creditCardRepository.findCreditCardsByUserID(userID);
    }

    public List<CreditCard> getCreditCardApplicationsByEmail(String email) {
        User user=getUserByEmail(email);
        return getCreditCardApplications(String.valueOf(user.getId()));
    }

    public CreditCard createCreditCardApplicationByEmail(String email, CreditCard creditCard) {
        User user=getUserByEmail(email);
        return createCreditCardApplication(creditCard,user);

    }








}
