package com.patika.akbankservice.service;

import com.patika.akbankservice.enums.ApplicationStatus;
import com.patika.akbankservice.enums.CardType;
import com.patika.akbankservice.exceptions.akbankServiceException;
import com.patika.akbankservice.model.CreditCard;
import com.patika.akbankservice.model.User;
import com.patika.akbankservice.producer.NotificationProducer;
import com.patika.akbankservice.producer.dto.NotificationDTO;
import com.patika.akbankservice.producer.enums.LogType;
import com.patika.akbankservice.producer.enums.SuccessType;
import com.patika.akbankservice.repository.CreditCardRepository;
import com.patika.akbankservice.service.constants.BankConstants;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.patika.akbankservice.service.ApplicationServiceTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreditCardServiceTest {


    @InjectMocks
    CreditCardService creditCardService;

    @Mock
    CreditCardRepository creditCardRepository;

    @Mock
    private NotificationProducer notificationProducer;

    static User userNotCustomer;
    static User userCustomer;

    @BeforeAll
    static void createUsersCustomer(){
        userCustomer =createTestUser(prepareCustomerUser());
        userNotCustomer = createTestUser(prepareNotCustomerUser());
        addUserToCustomers(userCustomer);

    }
    @Test
    void should_return_allCreditCardApplications_successfully() {//getAllCreditCardApplications
        //given
        Mockito.when(creditCardRepository.findAll()).thenReturn(prepareCreditCardList());

        //when
        List<CreditCard> returnAllCards = creditCardService.getAllCreditCardApplications();
        //then

        assertThat(returnAllCards).isNotNull();
        assertThat(returnAllCards.get(0).getCardNumber()).isEqualTo(prepareCreditCardList().get(0).getCardNumber());
        assertThat(returnAllCards.get(1).getCardLimit()).isEqualTo(prepareCreditCardList().get(1).getCardLimit());
        assertThat(returnAllCards.get(2).getExpiryDate()).isEqualTo(prepareCreditCardList().get(2).getExpiryDate());
        assertThat(returnAllCards.get(3).getCvv()).isEqualTo(prepareCreditCardList().get(3).getCvv());
        assertThat(returnAllCards.get(4).getUserID()).isEqualTo(prepareCreditCardList().get(4).getUserID());


        verify(creditCardRepository,times(1)).findAll();

    }

    @Test
    void should_return_allCreditCardApplicationsByUserID_successfully(){//getCreditCardApplications
        //given
        Mockito.when(creditCardRepository.findCreditCardsByUserID("999")).thenReturn(prepareCreditCardList().stream().filter(creditCard -> creditCard.getUserID().equals("999")).toList());

        //when
        List<CreditCard> returnCreditCards = creditCardService.getCreditCardApplications("999");

        //then
        assertThat(returnCreditCards).isNotNull();
        assertThat(returnCreditCards.size()).isEqualTo(1);
        assertThat(returnCreditCards.getLast().getUserID()).isEqualTo("999");


        verify(creditCardRepository,times(1)).findCreditCardsByUserID("999");

    }

    @Test
    void should_return_allCreditCardApplicationsByEmail_successfully(){//getCreditCardApplicationsByEmail
        Mockito.when(creditCardRepository.findCreditCardsByUserID(String.valueOf(userCustomer.getId()))).thenReturn(prepareCreditCardList().stream().filter(creditCard -> creditCard.getUserID().equals(String.valueOf(userCustomer.getId()))).toList());
        Mockito.when(notificationProducer.prepareNotificationDTO(LogType.READ, SuccessType.SUCCESS, "credit-cards", "Read all credit-card applications of user with userID:" +userCustomer.getId()+" for bankID:"+ BankConstants.bankID)).thenReturn(
                new NotificationDTO()
        );
        //when
        List<CreditCard> returnCreditCards = creditCardService.getCreditCardApplicationsByEmail(String.valueOf(userCustomer.getEmail()));
        //then

        assertThat(returnCreditCards).isNotNull();
        assertThat(returnCreditCards.size()).isEqualTo(3);
        assertThat(returnCreditCards.get(0).getUserID()).isEqualTo(String.valueOf(userCustomer.getId()));
        assertThat(returnCreditCards.get(0).getCardLimit()).isEqualTo(prepareCreditCardList().get(0).getCardLimit());
        assertThat(returnCreditCards.get(0).getExpiryDate()).isEqualTo(prepareCreditCardList().get(0).getExpiryDate());
        assertThat(returnCreditCards.get(1).getUserID()).isEqualTo(String.valueOf(userCustomer.getId()));
        assertThat(returnCreditCards.get(1).getCardLimit()).isEqualTo(prepareCreditCardList().get(1).getCardLimit());
        assertThat(returnCreditCards.get(1).getCvv()).isEqualTo(prepareCreditCardList().get(1).getCvv());
        assertThat(returnCreditCards.get(2).getUserID()).isEqualTo(String.valueOf(userCustomer.getId()));


        verify(creditCardRepository,times(1)).findCreditCardsByUserID(String.valueOf(userCustomer.getId()));
        verify(notificationProducer,times(1)).sendNotification(Mockito.any(NotificationDTO.class));
    }
    @Test
    void should_throw_exception_when_userNotFound_in_allCreditCardApplicationsByEmail(){//getCreditCardApplicationsByEmail
        Throwable throwable = catchThrowable(() -> creditCardService.getCreditCardApplicationsByEmail("test123"));
        assertThat(throwable).isInstanceOf(akbankServiceException.class);
        assertThat(throwable.getMessage()).isEqualTo("No user found with this email.");
    }

    @Test
    void should_create_creditCardApplication_successfully() {//createCreditCardApplication
        //given
        Mockito.when(creditCardRepository.save(Mockito.any(CreditCard.class))).thenAnswer(invocation -> {
            // çağrılan obje olduğu gibi return edilir
            return invocation.<User>getArgument(0);
        });

        //when
        CreditCard returnCreditCard= creditCardService.createCreditCardApplication(prepareCreditCard(),userCustomer);

        //then
        assertThat(returnCreditCard.getCardLimit()).isEqualTo(prepareCreditCard().getCardLimit());
        assertThat(returnCreditCard.getCardType()).isEqualTo(prepareCreditCard().getCardType());
        assertThat(returnCreditCard.getCvv()).isEqualTo(prepareCreditCard().getCvv());
        assertThat(returnCreditCard.getExpiryDate()).isEqualTo(prepareCreditCard().getExpiryDate());
        assertThat(returnCreditCard.getUserID()).isEqualTo(String.valueOf(userCustomer.getId()));

        verify(creditCardRepository,times(1)).save(Mockito.any(CreditCard.class));

    }

    @Test
    void should_throw_exception_when_user_is_not_customer_in_createCreditCardApplication() {//createCreditCardApplication
        Throwable throwable = catchThrowable(() -> creditCardService.createCreditCardApplication(prepareCreditCard(),userNotCustomer));
        assertThat(throwable).isInstanceOf(akbankServiceException.class);
        assertThat(throwable.getMessage()).isEqualTo("This user is not customer of this bank.(Otherwise check bankID)");

    }

    @Test
    void should_create_application_by_email_successfully_when_givenUser_IsCustomer(){//createCreditCardApplicationByEmail
        //given
        Mockito.when(creditCardRepository.save(Mockito.any(CreditCard.class))).thenAnswer(invocation -> {
            // çağrılan obje olduğu gibi return edilir
            return invocation.<User>getArgument(0);
        });

        //when
        CreditCard returnCreditCard= creditCardService.createCreditCardApplicationByEmail(userCustomer.getEmail(),prepareCreditCard());

        //then
        assertThat(returnCreditCard.getCardLimit()).isEqualTo(prepareCreditCard().getCardLimit());
        assertThat(returnCreditCard.getCardType()).isEqualTo(prepareCreditCard().getCardType());
        assertThat(returnCreditCard.getCvv()).isEqualTo(prepareCreditCard().getCvv());
        assertThat(returnCreditCard.getExpiryDate()).isEqualTo(prepareCreditCard().getExpiryDate());
        assertThat(returnCreditCard.getUserID()).isEqualTo(String.valueOf(userCustomer.getId()));

        verify(creditCardRepository,times(1)).save(Mockito.any(CreditCard.class));
    }

    @Test
    void should_throw_exception_when_givenUser_IsNotCustomer_in_createApplicationByEmail(){//createCreditCardApplicationByEmail
        Throwable throwable = catchThrowable(() -> creditCardService.createCreditCardApplicationByEmail(userNotCustomer.getEmail(),prepareCreditCard()));
        assertThat(throwable).isInstanceOf(akbankServiceException.class);
        assertThat(throwable.getMessage()).isEqualTo("This user is not customer of this bank.(Otherwise check bankID)");
    }

    @Test
    void should_throw_exception_when_givenUser_NotFound_in_createApplicationByEmail(){//createCreditCardApplicationByEmail
        Throwable throwable = catchThrowable(() -> creditCardService.createCreditCardApplicationByEmail(userNotCustomer.getEmail(),prepareCreditCard()));
        assertThat(throwable).isInstanceOf(akbankServiceException.class);
        assertThat(throwable.getMessage()).isEqualTo("This user is not customer of this bank.(Otherwise check bankID)");
    }



    private List<CreditCard> prepareCreditCardList() {
        List<CreditCard> creditCardList = new ArrayList<CreditCard>();

        CreditCard creditCard1 = new CreditCard();
        creditCard1.setCardNumber("1234 4567 7890 0123");
        creditCard1.setCvv(String.valueOf(456));
        creditCard1.setExpiryDate(LocalDate.of(2032,1,29));
        creditCard1.setCardLimit(BigDecimal.valueOf(7895));
        creditCard1.setUserID(String.valueOf(userCustomer.getId()));
        creditCard1.setCardType(CardType.BANK_CARD);
        creditCard1.setApplicationStatus(ApplicationStatus.INITIAL);
        creditCardList.add(creditCard1);

        CreditCard creditCard2 = new CreditCard();
        creditCard2.setCardNumber("4568 2135 7876 4564");
        creditCard2.setCvv(String.valueOf(789));
        creditCard2.setExpiryDate(LocalDate.of(2036,2,25));
        creditCard2.setCardLimit(BigDecimal.valueOf(4564));
        creditCard2.setUserID(String.valueOf(userCustomer.getId()));
        creditCard2.setCardType(CardType.PERSONAL);
        creditCard2.setApplicationStatus(ApplicationStatus.INITIAL);
        creditCardList.add(creditCard2);

        CreditCard creditCard3 = new CreditCard();
        creditCard3.setCardNumber("1235 7895 4568 2345");
        creditCard3.setCvv(String.valueOf(321));
        creditCard3.setExpiryDate(LocalDate.of(2028,6,9));
        creditCard3.setCardLimit(BigDecimal.valueOf(2345));
        creditCard3.setUserID(String.valueOf(61));
        creditCard3.setCardType(CardType.BANK_CARD);
        creditCard3.setApplicationStatus(ApplicationStatus.IN_PROGRESS);
        creditCardList.add(creditCard3);

        CreditCard creditCard4 = new CreditCard();
        creditCard4.setCardNumber("3257 2435 1023 7856");
        creditCard4.setCvv(String.valueOf(123));
        creditCard4.setExpiryDate(LocalDate.of(2030,7,19));
        creditCard4.setCardLimit(BigDecimal.valueOf(5768));
        creditCard4.setUserID(String.valueOf(userCustomer.getId()));
        creditCard4.setCardType(CardType.BUSINESS);
        creditCard4.setApplicationStatus(ApplicationStatus.INITIAL);
        creditCardList.add(creditCard4);

        CreditCard creditCard5 = new CreditCard();
        creditCard5.setCardNumber("5767 5775 7896 2634");
        creditCard5.setCvv(String.valueOf(666));
        creditCard5.setExpiryDate(LocalDate.of(2031,8,20));
        creditCard5.setCardLimit(BigDecimal.valueOf(5276));
        creditCard5.setUserID(String.valueOf(999));
        creditCard5.setCardType(CardType.BUSINESS);
        creditCard5.setApplicationStatus(ApplicationStatus.IN_PROGRESS);
        creditCardList.add(creditCard5);


        return creditCardList;



    }

    private CreditCard prepareCreditCard(){
        CreditCard creditCard = new CreditCard();
        creditCard.setCardNumber("1234 5678 9012 3456");
        creditCard.setCvv(String.valueOf(123));
        creditCard.setExpiryDate(LocalDate.of(2032,1,29));
        creditCard.setCardLimit(BigDecimal.valueOf(7895));
        creditCard.setCardType(CardType.BANK_CARD);
        return creditCard;
    }
    @AfterAll
    public static void cleanUp() {
        //first remove from customers, because it is connected with foreign key in database.
        removeCustomer(userCustomer.getEmail());
        deleteUser(userCustomer.getEmail());
        deleteUser(userNotCustomer.getEmail());
    }
}