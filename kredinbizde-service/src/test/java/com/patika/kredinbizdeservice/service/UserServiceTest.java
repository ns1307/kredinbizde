package com.patika.kredinbizdeservice.service;

import com.patika.kredinbizdeservice.exceptions.KredinbizdeException;
import com.patika.kredinbizdeservice.model.Address;
import com.patika.kredinbizdeservice.model.User;
import com.patika.kredinbizdeservice.producer.NotificationProducer;
import com.patika.kredinbizdeservice.producer.dto.NotificationDTO;
import com.patika.kredinbizdeservice.producer.enums.LogType;
import com.patika.kredinbizdeservice.producer.enums.SuccessType;
import com.patika.kredinbizdeservice.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationProducer notificationProducer;


    @Test
    public void should_create_user_successfully() {//save
        //given

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> {
            // çağrılan obje olduğu gibi return edilir
            return invocation.<User>getArgument(0);
        });
        Mockito.when(notificationProducer.prepareNotificationDTO(LogType.CREATE, SuccessType.SUCCESS, "users", "User created with ID:" + 0)).thenReturn(
                new NotificationDTO()
        );

        //when
        User userResponse = userService.save(prepareUser());

        //then
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getName()).isEqualTo(prepareUser().getName());
        assertThat(userResponse.getSurname()).isEqualTo(prepareUser().getSurname());
        assertThat(userResponse.getEmail()).isEqualTo(prepareUser().getEmail());
        assertThat(userResponse.getPassword()).isEqualTo(prepareUser().getPassword());
        assertThat(userResponse.getIsActive()).isEqualTo(prepareUser().getIsActive());


        verify(userRepository, times(1)).save(Mockito.any(User.class));
        verify(notificationProducer, times(1)).sendNotification(Mockito.any(NotificationDTO.class));
    }

    @Test
    void should_throw_kredinBizdeException_when_user_could_not_created() {
        //given
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenThrow(KredinbizdeException.class);

        //when
        Throwable throwable = catchThrowable(() -> userService.save(prepareUser()));

        //then
        assertThat(throwable).isInstanceOf(KredinbizdeException.class);
        assertThat(throwable.getMessage()).isEqualTo("User could not created with email:" + prepareUser().getEmail());

    }


    @Test
    void should_throw_kredinBizdeException_when_could_not_return_all_users() {
        //given
        Mockito.when(userRepository.findAll()).thenThrow(KredinbizdeException.class);

        //when
        Throwable throwable = catchThrowable(() -> userService.getAll());

        //then
        assertThat(throwable).isInstanceOf(KredinbizdeException.class);
        assertThat(throwable.getMessage()).isEqualTo("All users could not be read.");

    }

    @Test
    void should_return_user_by_email_successfully() {//getByEmail
        //given

        Mockito.when(userRepository.findByEmail("test1@gmail.com")).thenReturn(Optional.of(prepareUser()));


        //when
        User userResponse = userService.getByEmail("test1@gmail.com");

        //then
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getId()).isEqualTo(prepareUser().getId());
        assertThat(userResponse.getName()).isEqualTo(prepareUser().getName());
        assertThat(userResponse.getSurname()).isEqualTo(prepareUser().getSurname());
        assertThat(userResponse.getEmail()).isEqualTo(prepareUser().getEmail());
        assertThat(userResponse.getPassword()).isEqualTo(prepareUser().getPassword());
        assertThat(userResponse.getIsActive()).isEqualTo(prepareUser().getIsActive());


    }

    @Test
    void should_throw_kredinBizdeException_when_userNotFound_for_find_with_email() {
        Throwable throwable = catchThrowable(() -> userService.getByEmail("test"));
        assertThat(throwable).isInstanceOf(KredinbizdeException.class);
        assertThat(throwable.getMessage()).isEqualTo("No user found with this email.");

    }

    @Test
    void should_return_allUsers_when_getAllUsers() {//getAll method
        //given

        Mockito.when(userRepository.findAll()).thenReturn(prepareUserList());


        //when
        List<User> userListResponse = userService.getAll();

        //then
        assertThat(userListResponse).isNotNull();
        assertThat(userListResponse.size()).isEqualTo(prepareUserList().size());
        assertThat(userListResponse.get(0).getName()).isEqualTo(prepareUserList().get(0).getName());
        assertThat(userListResponse.get(1).getName()).isEqualTo(prepareUserList().get(1).getName());
        assertThat(userListResponse.get(2).getName()).isEqualTo(prepareUserList().get(2).getName());

        assertThat(userListResponse.get(0).getSurname()).isEqualTo(prepareUserList().get(0).getSurname());
        assertThat(userListResponse.get(1).getSurname()).isEqualTo(prepareUserList().get(1).getSurname());
        assertThat(userListResponse.get(2).getSurname()).isEqualTo(prepareUserList().get(2).getSurname());

        assertThat(userListResponse.get(0).getPassword()).isEqualTo(prepareUserList().get(0).getPassword());
        assertThat(userListResponse.get(1).getPassword()).isEqualTo(prepareUserList().get(1).getPassword());
        assertThat(userListResponse.get(2).getPassword()).isEqualTo(prepareUserList().get(2).getPassword());

        assertThat(userListResponse.get(0).getEmail()).isEqualTo(prepareUserList().get(0).getEmail());
        assertThat(userListResponse.get(1).getEmail()).isEqualTo(prepareUserList().get(1).getEmail());
        assertThat(userListResponse.get(2).getEmail()).isEqualTo(prepareUserList().get(2).getEmail());


        verify(userRepository, times(1)).findAll();

    }

    @Test
    void should_return_updatedUser_when_update_successfully() {

        //given
        Mockito.when(userRepository.findByEmail("test1@gmail.com")).thenReturn(Optional.of(prepareUser()));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> {
            // çağrılan obje olduğu gibi return edilir
            return invocation.<User>getArgument(0);
        });

        //when
        User userResponse = userService.update("test1@gmail.com", prepareUser2());


        //then
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getName()).isEqualTo(prepareUser2().getName());
        assertThat(userResponse.getSurname()).isEqualTo(prepareUser2().getSurname());
        assertThat(userResponse.getEmail()).isEqualTo(prepareUser2().getEmail());
        assertThat(userResponse.getPassword()).isEqualTo(prepareUser2().getPassword());
        assertThat(userResponse.getIsActive()).isEqualTo(prepareUser2().getIsActive());
        assertThat(userResponse.getAddress().getAddressTitle()).isEqualTo(prepareUser2().getAddress().getAddressTitle());
        assertThat(userResponse.getAddress().getAddressDescription()).isEqualTo(prepareUser2().getAddress().getAddressDescription());
        assertThat(userResponse.getAddress().getProvince()).isEqualTo(prepareUser2().getAddress().getProvince());
        verify(userRepository, times(1)).findByEmail(Mockito.any(String.class));
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void should_throw_kredinBizdeException_when_userNotFound_for_update() {
        Throwable throwable = catchThrowable(() -> userService.update("TESTEST", prepareUser2()));
        assertThat(throwable).isInstanceOf(KredinbizdeException.class);
        assertThat(throwable.getMessage()).isEqualTo("No user found with this email.");

    }

    @Test
    void should_delete_user_successfully() {
        //given
        Mockito.when(userRepository.findByEmail("test1@gmail.com")).thenReturn(Optional.of(prepareUser()));

        //when
        User userResponse = userService.deleteByEmail("test1@gmail.com");


        //then
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getName()).isEqualTo(prepareUser().getName());
        assertThat(userResponse.getSurname()).isEqualTo(prepareUser().getSurname());
        assertThat(userResponse.getEmail()).isEqualTo(prepareUser().getEmail());
        assertThat(userResponse.getPassword()).isEqualTo(prepareUser().getPassword());
        assertThat(userResponse.getIsActive()).isEqualTo(prepareUser().getIsActive());

        verify(userRepository, times(1)).findByEmail(Mockito.any(String.class));
        verify(userRepository, times(1)).delete(Mockito.any(User.class));


    }

    @Test
    void should_throw_exception_when_userNotFound_for_delete() {
        Throwable throwable = catchThrowable(() -> userService.deleteByEmail("TESTEST"));
        assertThat(throwable).isInstanceOf(KredinbizdeException.class);
        assertThat(throwable.getMessage()).isEqualTo("No user found with this email.");

    }


    private User prepareUser() {
        User user = new User();
        user.setId(0L);
        user.setEmail("test1@gmail.com");
        user.setName("Test1Name");
        user.setSurname("Test1Surname");
        user.setPassword("test1password");
        user.setIsActive(true);
        return user;
    }

    private User prepareUser2() {
        User user = new User();
        user.setEmail("test2@gmail.com");
        user.setName("Test2Name");
        user.setSurname("Test2Surname");
        user.setPassword("test2password");
        user.setIsActive(true);
        Address address = new Address();
        address.setAddressTitle("testEv");
        address.setAddressDescription("test mah. cad. posta kodu");
        address.setProvince("TESTX");
        user.setAddress(address);
        return user;
    }


    private List<User> prepareUserList() {

        List<User> userList = new ArrayList<>();
        User user1 = prepareUser();

        User user2 = prepareUser2();


        User user3 = new User();
        user3.setEmail("test3@gmail.com");
        user3.setName("Test3Name");
        user3.setSurname("Test3Surname");
        user3.setPassword("test3password");
        user3.setIsActive(true);


        userList.add(user1);
        userList.add(user2);
        userList.add(user3);

        return userList;

    }
}