package com.patika.kredinbizdeservice.service;

import com.patika.kredinbizdeservice.exceptions.KredinbizdeException;
import com.patika.kredinbizdeservice.model.User;
import com.patika.kredinbizdeservice.producer.NotificationProducer;
import com.patika.kredinbizdeservice.producer.dto.NotificationDTO;
import com.patika.kredinbizdeservice.producer.enums.LogType;
import com.patika.kredinbizdeservice.producer.enums.SuccessType;
import com.patika.kredinbizdeservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Scope(value = "singleton")
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final NotificationProducer notificationProducer;

    //@CacheEvict(value = "users", allEntries = true)
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = KredinbizdeException.class)
    public User save(User user) {
        user.setUpdateTime(LocalDateTime.now());
        user.setCreateTime(LocalDateTime.now());
        try {
            user = userRepository.save(user);
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.CREATE, SuccessType.SUCCESS, "users", "User created with ID:" + user.getId()));
            return user;
        } catch (Exception e) {
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.CREATE, SuccessType.FAIL, "users", "User could not created with email:" + user.getEmail()));
            throw new KredinbizdeException("User could not created with email:" + user.getEmail());
        }

    }


    public List<User> getAll() {
        try {
            List<User> userList = userRepository.findAll();
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.READ, SuccessType.SUCCESS, "users", "All users read."));
            return userList;
        } catch (Exception e) {
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.READ, SuccessType.FAIL, "users", "All users read failed."));
            throw new KredinbizdeException("All users could not be read.");
        }

    }

    public User getByEmail(String email) {

        Optional<User> foundUser = userRepository.findByEmail(email);


        if (foundUser.isPresent()) {
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.READ, SuccessType.SUCCESS, "users", "User info read by email:" + email));
            return foundUser.get();
        } else {
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.READ, SuccessType.FAIL, "users", "User info read by email failed :" + email));
            throw new KredinbizdeException("No user found with this email.");
        }

    }

    public User update(String email, User newUser) {

        Optional<User> foundUser = userRepository.findByEmail(email);

        if (foundUser.isPresent()) {
            User user = foundUser.get();
            if (!newUser.getName().isEmpty()) {
                user.setName(newUser.getName());
            }
            if (!newUser.getSurname().isEmpty()) {
                user.setSurname(newUser.getSurname());
            }
            if (!newUser.getPassword().isEmpty()) {
                user.setPassword(newUser.getPassword());
            }
            if (!newUser.getEmail().isEmpty()) {
                user.setEmail(newUser.getEmail());
            }
            if (newUser.getAddress() != null) {
                user.setAddress(newUser.getAddress());
            }
            user.setUpdateTime(LocalDateTime.now());
            user = userRepository.save(user);//save method can update object if it exists.
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.UPDATE, SuccessType.SUCCESS, "users", "User updated by email:" + email));
            return user;
        } else {
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.UPDATE, SuccessType.FAIL, "users", "User update by email failed:" + email));
            throw new KredinbizdeException("No user found with this email.");
        }


    }

    public User deleteByEmail(String email) {//used for deleting test users only
        Optional<User> foundUser = userRepository.findByEmail(email);

        if (foundUser.isPresent()) {

            User user = foundUser.get();
            userRepository.delete(user);
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.DELETE, SuccessType.SUCCESS, "users", "User deleted by email:" + email));
            return user;
        } else {
            notificationProducer.sendNotification(notificationProducer.prepareNotificationDTO(LogType.DELETE, SuccessType.FAIL, "users", "User delete by email failed:" + email));
            throw new KredinbizdeException("No user found with this email.");
        }

    }
}
