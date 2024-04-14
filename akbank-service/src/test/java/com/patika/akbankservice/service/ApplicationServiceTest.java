package com.patika.akbankservice.service;

import com.patika.akbankservice.enums.LoanType;
import com.patika.akbankservice.exceptions.akbankServiceException;
import com.patika.akbankservice.model.Bank;
import com.patika.akbankservice.model.Loan;
import com.patika.akbankservice.model.User;
import com.patika.akbankservice.service.constants.BankConstants;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {


    @InjectMocks
    private ApplicationService applicationService;

    //!!!IMPORTANT!!!!
    // The following tests all run successfully.
    // Since these tests involve database operations, they need to be executed sequentially.
    // @BeforeAll creates sample objects from kredinbizde database and @AfterAll deletes all created sample objects from database.
    // It would be better to run all test at once.

    //!!!!ÖNEMLİ!!!!
    // Aşağıdaki testlerin hepsi başarıyla çalışmaktadır.
    // Bu testler veritabanı işlemi içerdiği için sırayla çalıştırmak gerekmektedir.
    //@BeforeAll kredinbizde veritabanından örnek nesneler oluşturur ve @AfterAll oluşturulan tüm örnek nesneleri veritabanından siler.
    // Hepsinin birden çalıştırılması daha sağlıklı olur.

    static User userNotCustomer;
    static User userCustomer;
    @BeforeAll
    static void createUsersCustomer(){
        userCustomer =createTestUser(prepareCustomerUser());
        userNotCustomer = createTestUser(prepareNotCustomerUser());
        addUserToCustomers(userCustomer);

    }
    @AfterAll
    public static void cleanUp() {
        //first remove from customers, because it is connected with foreign key in database.
        removeCustomer(userNotCustomer.getEmail());
        deleteUser(userCustomer.getEmail());
        deleteUser(userNotCustomer.getEmail());
    }
    //-------SUCCESSFUL TESTS------------
    @Test
    void should_find_user_by_email_successfully() {
        //given

        //when
        User foundUser=applicationService.getUserByEmail(userCustomer.getEmail());

        //then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(userCustomer.getEmail());
        assertThat(foundUser.getName()).isEqualTo(userCustomer.getName());
        assertThat(foundUser.getSurname()).isEqualTo(userCustomer.getSurname());
        assertThat(foundUser.getPassword()).isEqualTo(userCustomer.getPassword());



    }
    @Test
    void should_add_customer_successfully() {
        //given
        //make sure you created test user.(Assuming testUser is created with createTestUser(prepareTestUser())  )
        //User testUser =createTestUser(prepareTestUser());

        //when
        Bank bankResponse =applicationService.addCustomerByEmail(userNotCustomer.getEmail());
        //then
        assertThat(bankResponse.getCustomers().stream().filter(customer -> customer.getEmail().equals(userNotCustomer.getEmail())).count()).isEqualTo(1);
        //make sure test user is added to the customers
    }



    @Test
    void should_check_if_user_is_customer_successfully() {
        //given
        //make sure you created test user.(Assuming testUser is created with createTestUser(prepareTestUser())  )
        //User testUser =createTestUser(prepareTestUser());

        //make sure you added test user to customers.(Assuming below code has executed.)
        //applicationService.addCustomerByEmail(prepareTestUser().getEmail());

        //when
        String userID= String.valueOf(applicationService.getUserByEmail(userCustomer.getEmail()).getId());//get ID from database
        User user = applicationService.isUserCustomer(userID);
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(userCustomer.getEmail());
        assertThat(user.getName()).isEqualTo(userCustomer.getName());
        assertThat(user.getPassword()).isEqualTo(userCustomer.getPassword());
        assertThat(user.getSurname()).isEqualTo(userCustomer.getSurname());

    }


    @Test
    void should_remove_customer_successfully() {
        //given
        //make sure you created test user.(Assuming testUser is created with createTestUser(prepareTestUser())  )
        //User testUser =createTestUser(prepareTestUser());

        //make sure you added test user to customers.(Assuming below code has executed.)
        //applicationService.addCustomerByEmail(prepareTestUser().getEmail());
        applicationService.addCustomerByEmail(userCustomer.getEmail());//this code can be runned more tham once, this won't cause a problem.
        //Because, if a user is already customer, he/she will not be added as a new customer again.
        //when
        Bank bankResponse =applicationService.removeCustomer(userCustomer.getEmail());
        //then
        assertThat(bankResponse.getCustomers().stream().filter(customer -> customer.getEmail().equals(userCustomer.getEmail())).count()).isEqualTo(0);
        //make sure test user is removed from customers


        //AFTER SUCCESSFULL TASKS DON'T FORGET TO DELETE TESTUSER FROM DATABASE
    }


    //--------EXCEPTION TESTS------------

    @Test
    void should_throw_exception_when_userNotFound_in_addCustomer() {

        Throwable throwable = catchThrowable(() -> applicationService.addCustomerByEmail("test"));
        assertThat(throwable).isInstanceOf(akbankServiceException.class);
        assertThat(throwable.getMessage()).isEqualTo("No user found with this email.");

    }

    @Test
    void should_throw_exception_when_costumerNotFound_in_removeCustomer() {
        Throwable throwable = catchThrowable(() -> applicationService.removeCustomer("test"));
        assertThat(throwable).isInstanceOf(akbankServiceException.class);
        assertThat(throwable.getMessage()).isEqualTo("Customer doesn't exist for this bank.");
    }



    @Test
    void should_throw_exception_when_userNotFound_by_email() {
        Throwable throwable = catchThrowable(() -> applicationService.getUserByEmail("test"));
        assertThat(throwable).isInstanceOf(akbankServiceException.class);
        assertThat(throwable.getMessage()).isEqualTo("No user found with this email.");


    }


    @Test
    void should_throw_exception_when_user_is_not_customer() {
        Throwable throwable = catchThrowable(() -> applicationService.isUserCustomer("9999"));
        assertThat(throwable).isInstanceOf(akbankServiceException.class);
        assertThat(throwable.getMessage()).isEqualTo("This user is not customer of this bank.(Otherwise check bankID)");

    }





    public static User prepareCustomerUser() {
        User user = new User();
        user.setName("Test00Name");
        user.setSurname("Test00Surname");
        user.setEmail("test00@mail.com");
        user.setPassword("test00Password");
        user.setBirthDate(LocalDate.now());

        return user;

    }
    public static User prepareNotCustomerUser() {
        User user = new User();
        user.setName("Test999Name");
        user.setSurname("Test999Surname");
        user.setEmail("test999@mail.com");
        user.setPassword("test999Password");
        user.setBirthDate(LocalDate.now());


        return user;
    }

    private Loan prepareLoan() {
        Loan loan  = new Loan();
        loan.setLoanType(LoanType.CONSUMER_LOAN);
        loan.setAmount(BigDecimal.valueOf(10000));
        loan.setInterestRate(BigDecimal.valueOf(5));
        return loan;

    }
    public static User createTestUser(User user) {

        return WebClient.builder().baseUrl("http://localhost:8084").build().post()
                .uri("/api/kredinbizde/users")
                .body(BodyInserters.fromValue(user))
                .retrieve()
                .onStatus(status -> status.value() == 500, response ->
                        response.bodyToMono(akbankServiceException.class)
                                .flatMap(errorResponse ->
                                        Mono.error(new akbankServiceException("User already exists with this email."))))
                .bodyToMono(User.class).block();
    }

    public static void addUserToCustomers(User user){
        WebClient.builder().baseUrl("http://localhost:8084").build().post() // GET isteği
                .uri("/api/kredinbizde/banks/"+ BankConstants.bankID+"/"+user.getEmail()) // URI ve path variable
                .retrieve()
                .onStatus(status -> status.value() == 404, response ->
                        response.bodyToMono(akbankServiceException.class)
                                .flatMap(errorResponse ->
                                        Mono.error(new akbankServiceException("No user found with this email."))))
                .bodyToMono(Bank.class).block();
    }



    public static void removeCustomer(String email) {
        WebClient.builder().baseUrl("http://localhost:8084").build().delete() // GET isteği
                .uri("/api/kredinbizde/banks/"+BankConstants.bankID+"/"+email) // URI ve path variable
                .retrieve()
                .onStatus(status -> status.value() == 404, response ->
                        response.bodyToMono(akbankServiceException.class)
                                .flatMap(errorResponse ->
                                        Mono.error(new akbankServiceException("Customer doesn't exist for this bank.User:"+email))))
                .bodyToMono(Bank.class).block();
    }
    public static void deleteUser(String email) {
        WebClient.builder().baseUrl("http://localhost:8084").build().delete() // GET isteği
                .uri("/api/kredinbizde/users/"+email) // URI ve path variable
                .retrieve()
                .onStatus(status -> status.value() == 404, response ->
                        response.bodyToMono(akbankServiceException.class)
                                .flatMap(errorResponse ->
                                        Mono.error(new akbankServiceException("User does not exist:"+email))))
                .bodyToMono(Bank.class).block();
    }

}