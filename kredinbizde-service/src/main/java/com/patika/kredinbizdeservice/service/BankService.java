package com.patika.kredinbizdeservice.service;

import com.patika.kredinbizdeservice.exceptions.ExceptionMessages;
import com.patika.kredinbizdeservice.exceptions.KredinbizdeException;
import com.patika.kredinbizdeservice.model.Bank;
import com.patika.kredinbizdeservice.model.User;
import com.patika.kredinbizdeservice.producer.NotificationProducer;
import com.patika.kredinbizdeservice.producer.enums.LogType;
import com.patika.kredinbizdeservice.producer.enums.SuccessType;
import com.patika.kredinbizdeservice.repository.BankRepository;
import com.patika.kredinbizdeservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Scope(value = "singleton")
@RequiredArgsConstructor
@Slf4j
public class BankService {

    private final BankRepository bankRepository;
    private final UserRepository userRepository;

    private final NotificationProducer notificationProducer;

    public Bank save(Bank bank) {

        try {
            bank.setUpdateTime(LocalDateTime.now());
            bank.setCreateTime(LocalDateTime.now());
            bank = bankRepository.save(bank);
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.CREATE, SuccessType.SUCCESS, "banks", "Bank created with ID:" + bank.getId() + " name:" + bank.getName()));
            return bank;
        } catch (Exception e) {
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.CREATE, SuccessType.FAIL, "banks", "Bank could not be created with name:" + bank.getName()));
            throw new KredinbizdeException("Bank could not be created.");

        }

    }


    public User findCustomerBanks(Long bankID, Long userID) {
        Optional<Bank> bankOptional = bankRepository.findById(bankID);
        if (bankOptional.isPresent()) {
            Bank bank = bankOptional.get();
            return findUserinCustomers(bank, userID);
        } else {
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.READ, SuccessType.FAIL, "banks", "Bank not found when checking user in customers with bankID:" + bankID));
            throw new KredinbizdeException("Bank not found");
        }
    }

    public User findUserinCustomers(Bank bank, Long customerID) {
        List<User> customers = bank.getCustomers();
        Optional<User> foundUser = customers.stream()
                .filter(user -> user.getId().equals(customerID))
                .findAny();


        if (foundUser.isPresent()) {
            User returnUser = foundUser.get();
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.READ, SuccessType.SUCCESS, "banks", "User is customer of the bank. BankID:" + bank.getId() + ", userID:" + customerID));
            return returnUser;
        } else {
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.READ, SuccessType.SUCCESS, "banks", "User is not customer of the bank. BankID:" + bank.getId() + ", userID:" + customerID));
            throw new KredinbizdeException("This user is not customer of this bank.");
        }


    }

    public Bank addCustomer(Long bankID, String email) {
        Optional<Bank> bankOptional = bankRepository.findById(bankID);
        Bank notFoundBank = bankOptional.orElseThrow(() -> new KredinbizdeException("Bank not found."));

        if (bankOptional.isPresent()) {
            Bank bank = bankOptional.get();
            Optional<User> foundUser = userRepository.findByEmail(email);

            if (foundUser.isPresent()) {
                User user = foundUser.get();
                boolean anyMatch = bank.getCustomers().stream().anyMatch(customer -> user.getEmail().equals(customer.getEmail()));
                if (!anyMatch) {
                    bank.getCustomers().add(user);
                    bank.setUpdateTime(LocalDateTime.now());
                    bank = bankRepository.save(bank);
                    notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.UPDATE, SuccessType.SUCCESS, "banks", "New customer added for bankID:" + bank.getId() + " with email:" + user.getEmail()));
                    return bank;
                } else {//if already exists, no change
                    notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.UPDATE, SuccessType.FAIL, "banks", "New customer attempted to add but user is already a customer for bankID:" + bank.getId() + " with email:" + user.getEmail()));
                    return bank;
                }

            } else {
                notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.UPDATE, SuccessType.FAIL, "banks", "New customer could not be added for bankID:" + bank.getId() + " with email:" + email));
                throw new KredinbizdeException("No user found with this email.");
            }

        }
        return notFoundBank;
    }

    public Bank deleteCustomer(Long bankID, String email) {
        Optional<Bank> bankOptional = bankRepository.findById(bankID);
        Bank notFoundBank = bankOptional.orElseThrow(() -> new KredinbizdeException("Bank not found"));

        if (bankOptional.isPresent()) {
            Bank bank = bankOptional.get();
            Optional<User> foundUser = userRepository.findByEmail(email);

            if (foundUser.isPresent()) {
                User user = foundUser.get();
                List<User> customers = bank.getCustomers();
                boolean isCustomer = bank.getCustomers().stream().anyMatch(customer -> user.getEmail().equals(customer.getEmail()));
                if (isCustomer) {
                    customers.removeIf(customer -> user.getEmail().equals(customer.getEmail()));
                    bank.setCustomers(customers);
                    bank.setUpdateTime(LocalDateTime.now());
                    bank = bankRepository.save(bank);
                    notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.DELETE, SuccessType.SUCCESS, "banks", "Customer removed for bankID:" + bank.getId() + " with email:" + email));
                    return bank;
                } else {
                    notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.DELETE, SuccessType.FAIL, "banks", "Customer could not removed, because not a customer for bankID:" + bank.getId() + " with email:" + email));
                    throw new KredinbizdeException("User is not customer of this bank.");
                }

            } else {
                notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.DELETE, SuccessType.FAIL, "banks", "Customer could not removed, because no user exists with this email:" + email));
                throw new KredinbizdeException("User not found with this email.");
            }

        }
        return notFoundBank;
    }

    public List<User> getAllCustomers(Long bankID) {
        Optional<Bank> bankOptional = bankRepository.findById(bankID);
        if (bankOptional.isPresent()) {
            List<User> customers = bankOptional.get().getCustomers();
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.READ, SuccessType.SUCCESS, "banks", "All customers read for bank with bankID:" + bankID));
            return customers;
        } else {
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.READ, SuccessType.FAIL, "banks", "All customers attempted to read, but no bank found with bankID:" + bankID));
            throw new KredinbizdeException("Bank not found with this ID.");
        }
    }
}
