package com.patika.kredinbizdeservice.service;

import com.patika.kredinbizdeservice.exceptions.KredinbizdeException;
import com.patika.kredinbizdeservice.model.Address;
import com.patika.kredinbizdeservice.model.Bank;
import com.patika.kredinbizdeservice.model.User;
import com.patika.kredinbizdeservice.producer.NotificationProducer;
import com.patika.kredinbizdeservice.producer.dto.NotificationDTO;
import com.patika.kredinbizdeservice.producer.enums.LogType;
import com.patika.kredinbizdeservice.producer.enums.SuccessType;
import com.patika.kredinbizdeservice.repository.BankRepository;
import com.patika.kredinbizdeservice.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @InjectMocks
    private BankService bankService;

    @Mock
    private BankRepository bankRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationProducer notificationProducer;

    @Test
    void should_create_bank_successfully() {
        //given
        Mockito.when(bankRepository.save(Mockito.any(Bank.class))).thenAnswer(invocation -> {
            // çağrılan obje olduğu gibi return edilir
            return invocation.<User>getArgument(0);
        });

        //when
        Bank bankResponse = bankService.save(prepareBank());

        //then
        assertThat(bankResponse).isNotNull();
        assertThat(bankResponse.getAddress().getAddressDescription()).isEqualTo(prepareBank().getAddress().getAddressDescription());
        assertThat(bankResponse.getAddress().getAddressTitle()).isEqualTo(prepareBank().getAddress().getAddressTitle());
        assertThat(bankResponse.getName()).isEqualTo(prepareBank().getName());
        assertThat(bankResponse.getPhone()).isEqualTo(prepareBank().getPhone());
        assertThat(bankResponse.getCustomers().get(0).getEmail()).isEqualTo(prepareBank().getCustomers().get(0).getEmail());
        assertThat(bankResponse.getCustomers().get(1).getEmail()).isEqualTo(prepareBank().getCustomers().get(1).getEmail());

        verify(bankRepository, times(1)).save(Mockito.any(Bank.class));

    }

    @Test
    void should_throw_kredinBizdeException_when_bank_could_not_created() {
        //given
        Mockito.when(bankRepository.save(Mockito.any(Bank.class))).thenThrow(KredinbizdeException.class);

        //when
        Throwable throwable = catchThrowable(() -> bankService.save(prepareBank()));

        //then
        assertThat(throwable).isInstanceOf(KredinbizdeException.class);
        assertThat(throwable.getMessage()).isEqualTo("Bank could not be created.");

    }

    @Test
    void should_return_all_customers_in_bank() {
        //given
        Mockito.when(bankRepository.findById(0L)).thenReturn(Optional.of(prepareBank()));
        //when
        List<User> customersResponse = bankService.getAllCustomers(0L);


        assertThat(customersResponse).isNotNull();
        assertThat(customersResponse.size()).isEqualTo(prepareBank().getCustomers().size());
        assertThat(customersResponse.get(0).getName()).isEqualTo(prepareBank().getCustomers().get(0).getName());
        assertThat(customersResponse.get(1).getName()).isEqualTo(prepareBank().getCustomers().get(1).getName());
        assertThat(customersResponse.get(2).getName()).isEqualTo(prepareBank().getCustomers().get(2).getName());

        assertThat(customersResponse.get(0).getEmail()).isEqualTo(prepareBank().getCustomers().get(0).getEmail());
        assertThat(customersResponse.get(1).getEmail()).isEqualTo(prepareBank().getCustomers().get(1).getEmail());
        assertThat(customersResponse.get(2).getEmail()).isEqualTo(prepareBank().getCustomers().get(2).getEmail());

        verify(bankRepository, times(1)).findById(0L);

    }

    @Test
    void should_throw_exception_when_return_customers_in_getAllCustomers() {
        Throwable throwable = catchThrowable(() -> bankService.getAllCustomers(10L));
        assertThat(throwable).isInstanceOf(KredinbizdeException.class);
        assertThat(throwable.getMessage()).isEqualTo("Bank not found with this ID.");
    }


    @Test
    void should_return_user_from_customers() {//findUserinCustomers
        //given

        //when
        User returnUser = bankService.findUserinCustomers(prepareBank(), prepareCustomerList().get(1).getId());

        //then
        assertThat(returnUser).isNotNull();
        assertThat(returnUser.getName()).isEqualTo(prepareCustomerList().get(1).getName());
        assertThat(returnUser.getEmail()).isEqualTo(prepareCustomerList().get(1).getEmail());
        assertThat(returnUser.getSurname()).isEqualTo(prepareCustomerList().get(1).getSurname());
        assertThat(returnUser.getPassword()).isEqualTo(prepareCustomerList().get(1).getPassword());

    }

    @Test
    void should_throw_exception_when_userNotFound_in_customers() {//findUserinCustomers
        Throwable throwable = catchThrowable(() -> bankService.findUserinCustomers(prepareBank(), 5L));
        assertThat(throwable).isInstanceOf(KredinbizdeException.class);
        assertThat(throwable.getMessage()).isEqualTo("This user is not customer of this bank.");
    }

    @Test
    void should_return_user_from_bankCustomers() {//findCustomerBanks
        //given
        Mockito.when(bankRepository.findById(0L)).thenReturn(Optional.of(prepareBank()));

        //when
        User returnUser = bankService.findCustomerBanks(0L, prepareBank().getCustomers().get(0).getId());
        //then
        assertThat(returnUser).isNotNull();
        assertThat(returnUser.getId()).isEqualTo(prepareBank().getCustomers().get(0).getId());
        assertThat(returnUser.getName()).isEqualTo(prepareBank().getCustomers().get(0).getName());
        assertThat(returnUser.getEmail()).isEqualTo(prepareBank().getCustomers().get(0).getEmail());
        assertThat(returnUser.getPassword()).isEqualTo(prepareBank().getCustomers().get(0).getPassword());
        assertThat(returnUser.getSurname()).isEqualTo(prepareBank().getCustomers().get(0).getSurname());
        assertThat(returnUser.getPassword()).isEqualTo(prepareBank().getCustomers().get(0).getPassword());


        verify(bankRepository, times(1)).findById(0L);

    }

    @Test
    void should_throw_exception_when_bankNotFound_in_bankCustomers() {//findCustomerBanks

        Throwable throwable = catchThrowable(() -> bankService.findCustomerBanks(0L, 5L));
        assertThat(throwable).isInstanceOf(KredinbizdeException.class);
        assertThat(throwable.getMessage()).isEqualTo("Bank not found");
    }


    @Test
    void should_add_user_to_customers_of_bank_successfully_when_user_isnot_customer() {//addCustomer
        //given
        Mockito.when(bankRepository.findById(0L)).thenReturn(Optional.of(prepareBank()));
        Mockito.when(userRepository.findByEmail("TESTXUSER@gmail.com")).thenReturn(Optional.of(prepareUserNotCustomer()));
        Mockito.when(bankRepository.save(Mockito.any(Bank.class))).thenAnswer(invocation -> {
            // çağrılan obje olduğu gibi return edilir
            return invocation.<User>getArgument(0);
        });

        //when
        Bank returnBank = bankService.addCustomer(0L, "TESTXUSER@gmail.com");


        //then
        assertThat(returnBank).isNotNull();
        assertThat(returnBank.getName()).isEqualTo(prepareBank().getName());
        assertThat(returnBank.getPhone()).isEqualTo(prepareBank().getPhone());
        //customers içerisinde test objesi kaç adet var?
        assertThat(returnBank.getCustomers().stream().filter(customer -> "TESTXUSER@gmail.com".equals(customer.getEmail())).count()).isEqualTo(1);

        verify(bankRepository, times(1)).findById(0L);
        verify(userRepository, times(1)).findByEmail("TESTXUSER@gmail.com");
        verify(bankRepository, times(1)).save(Mockito.any(Bank.class));
    }

    @Test
    void should_make_no_change_in_addingUserToCustomers_when_user_is_already_customer() {//addCustomer
        //given
        Mockito.when(bankRepository.findById(0L)).thenReturn(Optional.of(prepareBank()));
        Mockito.when(userRepository.findByEmail("TESTUSER1@gmail.com")).thenReturn(Optional.of(prepareUserCustomer()));
        Mockito.when(notificationProducer.prepareNotificationDTO(LogType.UPDATE, SuccessType.FAIL, "banks", "New customer attempted to add but user is already a customer for bankID:0 with email:" + prepareUserCustomer().getEmail())).thenReturn(
                new NotificationDTO()
        );

        //when
        Bank returnBank = bankService.addCustomer(0L, "TESTUSER1@gmail.com");


        //then
        assertThat(returnBank).isNotNull();
        assertThat(returnBank.getName()).isEqualTo(prepareBank().getName());
        assertThat(returnBank.getPhone()).isEqualTo(prepareBank().getPhone());
        //customers içerisinde test objesi kaç adet var?
        assertThat(returnBank.getCustomers().stream().filter(customer -> "TESTUSER1@gmail.com".equals(customer.getEmail())).count()).isEqualTo(1);

        verify(bankRepository, times(1)).findById(0L);
        verify(userRepository, times(1)).findByEmail("TESTUSER1@gmail.com");
        verify(bankRepository, times(0)).save(Mockito.any(Bank.class));

    }

    @Test
    void should_trow_exception_when_userNotFound_in_addCustomer() {//addCustomer
        Mockito.when(bankRepository.findById(0L)).thenReturn(Optional.of(prepareBank()));

        Throwable throwable = catchThrowable(() -> bankService.addCustomer(0L, "TESTUSER1@gmail.com"));

        assertThat(throwable).isInstanceOf(KredinbizdeException.class);
        assertThat(throwable.getMessage()).isEqualTo("No user found with this email.");
        verify(bankRepository, times(1)).findById(0L);
        verify(userRepository, times(1)).findByEmail("TESTUSER1@gmail.com");

    }

    @Test
    void should_trow_exception_when_bankNotFound_in_addCustomer() {//addCustomer
        Throwable throwable = catchThrowable(() -> bankService.addCustomer(0L, "TESTUSER1@gmail.com"));
        assertThat(throwable).isInstanceOf(KredinbizdeException.class);
        assertThat(throwable.getMessage()).isEqualTo("Bank not found.");
    }

    @Test
    void should_delete_user_from_customers_successfully() {//deleteCustomer
        //given
        Mockito.when(bankRepository.findById(0L)).thenReturn(Optional.of(prepareBank()));
        Mockito.when(userRepository.findByEmail("TESTUSER1@gmail.com")).thenReturn(Optional.of(prepareUserCustomer()));
        Mockito.when(bankRepository.save(Mockito.any(Bank.class))).thenAnswer(invocation -> {
            // çağrılan obje olduğu gibi return edilir
            return invocation.<User>getArgument(0);
        });

        //when
        Bank returnBank = bankService.deleteCustomer(0L, "TESTUSER1@gmail.com");


        //then
        assertThat(returnBank).isNotNull();
        assertThat(returnBank.getName()).isEqualTo(prepareBank().getName());
        assertThat(returnBank.getPhone()).isEqualTo(prepareBank().getPhone());
        //customers içerisinde test objesi kaç adet var?
        assertThat(returnBank.getCustomers().stream().filter(customer -> "TESTUSER1@gmail.com".equals(customer.getEmail())).count()).isEqualTo(0);

        verify(bankRepository, times(1)).findById(0L);
        verify(userRepository, times(1)).findByEmail("TESTUSER1@gmail.com");
        verify(bankRepository, times(1)).save(Mockito.any(Bank.class));
    }

    @Test
    void should_throw_exception_when_userIsNotCustomer_of_this_bank_in_deleteCustomer() {//deleteCustomer
        //given
        Mockito.when(bankRepository.findById(0L)).thenReturn(Optional.of(prepareBank()));
        Mockito.when(userRepository.findByEmail("TESTXUSER@gmail.com")).thenReturn(Optional.of(prepareUserNotCustomer()));


        //when
        Throwable throwable = catchThrowable(() -> bankService.deleteCustomer(0L, "TESTXUSER@gmail.com"));


        //customers içerisinde test objesi kaç adet var?

        assertThat(throwable).isInstanceOf(KredinbizdeException.class);
        assertThat(throwable.getMessage()).isEqualTo("User is not customer of this bank.");

        verify(bankRepository, times(1)).findById(0L);
        verify(userRepository, times(1)).findByEmail("TESTXUSER@gmail.com");
        verify(bankRepository, times(0)).save(Mockito.any(Bank.class));

    }


    @Test
    void should_throw_exception_when_userNotFound_in_deleteCustomer() {//deleteCustomer
        //given
        Mockito.when(bankRepository.findById(0L)).thenReturn(Optional.of(prepareBank()));


        //when
        Throwable throwable = catchThrowable(() -> bankService.deleteCustomer(0L, "TESTXUSER@gmail.com"));


        //customers içerisinde test objesi kaç adet var?
        assertThat(throwable).isInstanceOf(KredinbizdeException.class);
        assertThat(throwable.getMessage()).isEqualTo("User not found with this email.");
        verify(bankRepository, times(1)).findById(0L);
        verify(userRepository, times(1)).findByEmail("TESTXUSER@gmail.com");

    }

    @Test
    void should_trow_exception_when_bankNotFound_in_deleteCustomer() {//deleteCustomer

        Throwable throwable = catchThrowable(() -> bankService.deleteCustomer(10L, "TESTXUSER@gmail.com"));
        assertThat(throwable).isInstanceOf(KredinbizdeException.class);
        assertThat(throwable.getMessage()).isEqualTo("Bank not found");
    }

    private Bank prepareBank() {
        Bank bank = new Bank();
        bank.setId(0L);
        bank.setName("TESTBANK");
        bank.setCustomers(prepareCustomerList());
        bank.setPhone("444 44 44");
        Address address = new Address();
        address.setAddressTitle("testEv");
        address.setAddressDescription("test mah. cad. posta kodu");
        address.setProvince("TESTX");
        bank.setAddress(address);

        return bank;
    }

    private User prepareUserCustomer() {
        User user = new User();
        user.setName("TESTUSER1");
        user.setSurname("TEST1SURNAME");
        user.setEmail("TESTUSER1@gmail.com");

        return user;
    }

    private User prepareUserNotCustomer() {
        User user = new User();
        user.setName("TESTUSERX");
        user.setSurname("TESTXSURNAME");
        user.setEmail("TESTXUSER@gmail.com");

        return user;
    }

    private List<User> prepareCustomerList() {

        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setId(0L);
        user1.setName("TESTUSER1");
        user1.setSurname("TEST1SURNAME");
        user1.setEmail("TESTUSER1@gmail.com");
        user1.setPassword("1234");

        User user2 = new User();
        user2.setName("TESTUSER2");
        user2.setId(1L);
        user2.setSurname("TEST2SURNAME");
        user2.setEmail("TESTUSER2@gmail.com");
        user2.setPassword("1234");


        User user3 = new User();
        user3.setEmail("test3@gmail.com");
        user3.setName("Test3Name");
        user3.setId(2L);
        user3.setSurname("Test3Surname");
        user3.setPassword("test3password");
        user3.setIsActive(true);


        userList.add(user1);
        userList.add(user2);
        userList.add(user3);

        return userList;

    }
}