package com.patika.akbankservice.service;

import com.patika.akbankservice.model.Loan;
import com.patika.akbankservice.model.User;
import com.patika.akbankservice.enums.ApplicationStatus;
import com.patika.akbankservice.producer.NotificationProducer;
import com.patika.akbankservice.producer.enums.LogType;
import com.patika.akbankservice.producer.enums.SuccessType;
import com.patika.akbankservice.repository.LoanRepository;
import com.patika.akbankservice.service.constants.BankConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Scope(value = "singleton")
@RequiredArgsConstructor
@Slf4j
public class LoanService extends   ApplicationService{

    private final LoanRepository loanRepository;

    private final NotificationProducer notificationProducer;


    public List<Loan> getAllLoanApplications() {
        notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.READ, SuccessType.SUCCESS, "credit-cards", "Read all loan applications for bankID: "+BankConstants.bankID));
        return loanRepository.findAll();
    }

    public Loan createLoanApplication(Loan loan, User user) {
        loan.setCreatedDate(LocalDate.now());
        loan.setUpdatedDate(LocalDate.now());
        loan.setApplicationStatus(ApplicationStatus.INITIAL);
        loan.setUserID(String.valueOf(user.getId()));
        isUserCustomer(loan.getUserID());
        notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.CREATE, SuccessType.SUCCESS, "loans", "New loan application for user with userID:" +user.getId()+" for bankID:"+ BankConstants.bankID));
        return loanRepository.save(loan);
    }

    public List<Loan> getLoanApplications(String userID) {
        notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.READ, SuccessType.SUCCESS, "loans", "Read all loan applications of user with userID:" +userID+" for bankID:"+ BankConstants.bankID));
        return loanRepository.findLoansByUserID(userID);
    }

    public List<Loan> getLoanApplicationsByEmail(String email) {
        User user=getUserByEmail(email);
        return getLoanApplications(String.valueOf(user.getId()));
    }

    public Loan createLoanApplicationByEmail(String email, Loan loan) {
        User user=getUserByEmail(email);
        return createLoanApplication(loan,user);
    }

}
