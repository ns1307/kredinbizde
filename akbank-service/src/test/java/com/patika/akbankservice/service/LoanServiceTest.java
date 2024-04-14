package com.patika.akbankservice.service;

import com.patika.akbankservice.enums.ApplicationStatus;
import com.patika.akbankservice.enums.LoanType;
import com.patika.akbankservice.exceptions.akbankServiceException;
import com.patika.akbankservice.model.Loan;
import com.patika.akbankservice.model.User;
import com.patika.akbankservice.producer.NotificationProducer;
import com.patika.akbankservice.producer.dto.NotificationDTO;
import com.patika.akbankservice.producer.enums.LogType;
import com.patika.akbankservice.producer.enums.SuccessType;
import com.patika.akbankservice.repository.LoanRepository;
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
import java.util.ArrayList;
import java.util.List;

import static com.patika.akbankservice.service.ApplicationServiceTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @InjectMocks
    LoanService loanService;

    @Mock
    LoanRepository loanRepo;

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
    void should_return_allLoanApplications_successfully() {//getAllLoanApplications
        //given
        Mockito.when(loanRepo.findAll()).thenReturn(prepareLoanList());

        //when
        List<Loan> returnAllLoans = loanService.getAllLoanApplications();
        //then

        assertThat(returnAllLoans).isNotNull();
        assertThat(returnAllLoans.get(0).getLoanId()).isEqualTo(prepareLoanList().get(0).getLoanId());
        assertThat(returnAllLoans.get(1).getAmount()).isEqualTo(prepareLoanList().get(1).getAmount());
        assertThat(returnAllLoans.get(2).getLoanType()).isEqualTo(prepareLoanList().get(2).getLoanType());
        assertThat(returnAllLoans.get(0).getUserID()).isEqualTo(prepareLoanList().get(0).getUserID());
        assertThat(returnAllLoans.get(4).getLoanId()).isEqualTo(prepareLoanList().get(4).getLoanId());
        verify(loanRepo,times(1)).findAll();
    }

    @Test
    void should_return_allLoanApplicationsByUserID_successfully(){//getLoanApplications
        //given
        Mockito.when(loanRepo.findLoansByUserID("999")).thenReturn(prepareLoanList().stream().filter(loan -> loan.getUserID().equals("999")).toList());

        //when
        List<Loan> returnAllLoans = loanService.getLoanApplications("999");
        //then

        assertThat(returnAllLoans).isNotNull();
        assertThat(returnAllLoans.size()).isEqualTo(1);
        assertThat(returnAllLoans.get(0).getUserID()).isEqualTo("999");


        verify(loanRepo,times(1)).findLoansByUserID("999");

    }


    @Test
    void should_return_allLoanApplicationsByEmail_successfully(){//getLoanApplicationsByEmail
        Mockito.when(loanRepo.findLoansByUserID(String.valueOf(userCustomer.getId()))).thenReturn(prepareLoanList().stream().filter(loan -> loan.getUserID().equals(String.valueOf(userCustomer.getId()))).toList());
        Mockito.when(notificationProducer.prepareNotificationDTO(LogType.READ, SuccessType.SUCCESS, "loans", "Read all loan applications of user with userID:" +userCustomer.getId()+" for bankID:"+ BankConstants.bankID)).thenReturn(
                new NotificationDTO()
        );
        //when
        List<Loan> returnAllLoans = loanService.getLoanApplicationsByEmail(String.valueOf(userCustomer.getEmail()));
        //then

        assertThat(returnAllLoans).isNotNull();
        assertThat(returnAllLoans.size()).isEqualTo(3);
        assertThat(returnAllLoans.get(0).getUserID()).isEqualTo(String.valueOf(userCustomer.getId()));
        assertThat(returnAllLoans.get(0).getAmount()).isEqualTo(prepareLoanList().get(0).getAmount());
        assertThat(returnAllLoans.get(1).getUserID()).isEqualTo(String.valueOf(userCustomer.getId()));
        assertThat(returnAllLoans.get(1).getAmount()).isEqualTo(prepareLoanList().get(1).getAmount());
        assertThat(returnAllLoans.get(2).getUserID()).isEqualTo(String.valueOf(userCustomer.getId()));

        verify(loanRepo,times(1)).findLoansByUserID(String.valueOf(userCustomer.getId()));

    }

    @Test
    void should_throw_exception_when_userNotFound_in_allLoanApplicationsByEmail(){//getLoanApplicationsByEmail
        Throwable throwable = catchThrowable(() -> loanService.getLoanApplicationsByEmail("test123"));
        assertThat(throwable).isInstanceOf(akbankServiceException.class);
        assertThat(throwable.getMessage()).isEqualTo("No user found with this email.");

    }


    @Test
    void should_create_loanApplication_successfully() {//createLoanApplication
        //given
        Mockito.when(loanRepo.save(Mockito.any(Loan.class))).thenAnswer(invocation -> {
            // çağrılan obje olduğu gibi return edilir
            return invocation.<User>getArgument(0);
        });

        //when
        Loan returnLoan= loanService.createLoanApplication(prepareLoan(),userCustomer);

        //then
        assertThat(returnLoan.getAmount()).isEqualTo(prepareLoan().getAmount());
        assertThat(returnLoan.getLoanType()).isEqualTo(prepareLoan().getLoanType());
        assertThat(returnLoan.getUserID()).isEqualTo(String.valueOf(userCustomer.getId()));

        verify(loanRepo,times(1)).save(Mockito.any(Loan.class));

    }

    @Test
    void should_throw_exception_when_user_is_not_customer_in_createLoanApplication() {//createLoanApplication
        Throwable throwable = catchThrowable(() -> loanService.createLoanApplication(prepareLoan(),userNotCustomer));
        assertThat(throwable).isInstanceOf(akbankServiceException.class);
        assertThat(throwable.getMessage()).isEqualTo("This user is not customer of this bank.(Otherwise check bankID)");

    }


    @Test
    void should_create_application_by_email_successfully_when_givenUser_IsCustomer(){//createLoanApplicationByEmail
        //given
        Mockito.when(loanRepo.save(Mockito.any(Loan.class))).thenAnswer(invocation -> {
            // çağrılan obje olduğu gibi return edilir
            return invocation.<User>getArgument(0);
        });

        //when
        Loan returnLoan= loanService.createLoanApplicationByEmail(userCustomer.getEmail(),prepareLoan());

        //then
        assertThat(returnLoan.getAmount()).isEqualTo(prepareLoan().getAmount());
        assertThat(returnLoan.getLoanType()).isEqualTo(prepareLoan().getLoanType());
        assertThat(returnLoan.getUserID()).isEqualTo(String.valueOf(userCustomer.getId()));

        verify(loanRepo,times(1)).save(Mockito.any(Loan.class));
    }

    @Test
    void should_throw_exception_when_givenUser_IsNotCustomer_in_createApplicationByEmail(){//createLoanApplicationByEmail
        Throwable throwable = catchThrowable(() -> loanService.createLoanApplicationByEmail(userNotCustomer.getEmail(),prepareLoan()));
        assertThat(throwable).isInstanceOf(akbankServiceException.class);
        assertThat(throwable.getMessage()).isEqualTo("This user is not customer of this bank.(Otherwise check bankID)");
    }

    @Test
    void should_throw_exception_when_givenUser_NotFound_in_createApplicationByEmail(){//createLoanApplicationByEmail
        Throwable throwable = catchThrowable(() -> loanService.createLoanApplicationByEmail(userNotCustomer.getEmail(),prepareLoan()));
        assertThat(throwable).isInstanceOf(akbankServiceException.class);
        assertThat(throwable.getMessage()).isEqualTo("This user is not customer of this bank.(Otherwise check bankID)");
    }





    private List<Loan> prepareLoanList() {
        List<Loan> loanList = new ArrayList<>();

        Loan loan1 = new Loan();
        loan1.setUserID(String.valueOf(userCustomer.getId()));
        loan1.setLoanType(LoanType.CONSUMER_LOAN);
        loan1.setAmount(BigDecimal.valueOf(10000));
        loan1.setApplicationStatus(ApplicationStatus.INITIAL);
        loan1.setInterestRate(BigDecimal.valueOf(5));
        loanList.add(loan1);

        Loan loan2 = new Loan();
        loan2.setUserID(String.valueOf(userCustomer.getId()));
        loan2.setLoanType(LoanType.HOLIDAY_LOAN);
        loan2.setAmount(BigDecimal.valueOf(7857));
        loan2.setApplicationStatus(ApplicationStatus.INITIAL);
        loan2.setInterestRate(BigDecimal.valueOf(11));
        loanList.add(loan2);

        Loan loan3 = new Loan();
        loan3.setUserID(String.valueOf(999));
        loan3.setLoanType(LoanType.HOUSE_LOAN);
        loan3.setAmount(BigDecimal.valueOf(45754));
        loan3.setApplicationStatus(ApplicationStatus.IN_PROGRESS);
        loan3.setInterestRate(BigDecimal.valueOf(3));
        loanList.add(loan3);

        Loan loan4 = new Loan();
        loan4.setUserID(String.valueOf(userCustomer.getId()));
        loan4.setLoanType(LoanType.VEHICLE_LOAN);
        loan4.setAmount(BigDecimal.valueOf(6767));
        loan4.setApplicationStatus(ApplicationStatus.INITIAL);
        loan4.setInterestRate(BigDecimal.valueOf(1));
        loanList.add(loan4);

        Loan loan5 = new Loan();
        loan5.setUserID(String.valueOf(859));
        loan5.setLoanType(LoanType.CONSUMER_LOAN);
        loan5.setAmount(BigDecimal.valueOf(56456));
        loan5.setApplicationStatus(ApplicationStatus.INITIAL);
        loan5.setInterestRate(BigDecimal.valueOf(9));
        loanList.add(loan5);
        return loanList;
    }



    private Loan prepareLoan() {
        Loan loan  = new Loan();
        loan.setLoanType(LoanType.CONSUMER_LOAN);
        loan.setAmount(BigDecimal.valueOf(10000));
        loan.setInterestRate(BigDecimal.valueOf(5));
        return loan;

    }
    @AfterAll
    public static void cleanUp() {
        //first remove from customers, because it is connected with foreign key in database.
        removeCustomer(userCustomer.getEmail());
        deleteUser(userCustomer.getEmail());
        deleteUser(userNotCustomer.getEmail());
    }

}