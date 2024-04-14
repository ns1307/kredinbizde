package com.patika.kredinbizdeservice.repository;

import com.patika.kredinbizdeservice.model.Bank;

import com.patika.kredinbizdeservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankRepository extends JpaRepository<Bank, Long> {

    /*@Query("SELECT b FROM Bank b JOIN b.customers c WHERE c.id = :userId")
    List<Bank> findByCustomerId(@Param("userId") Long userId);*/

}
