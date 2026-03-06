package api.tests_level_senior.iteration_2;

import constants.api.Message;
import constants.api.TestDataConstants;
import generators.RandomData;
import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.steps.AccountSteps;
import requests.steps.AdminSteps;
import requests.steps.DepositSteps;

import java.util.stream.Stream;

public class DepositTest extends BaseTest {
    @DisplayName("Успешное пополнение аккаунта")
    @Test
    public void depositTest(){
        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();

        UserCreateAccountResponse account = AccountSteps.createAccount(user.getUsername(),AdminSteps.getOriginalPassword(username));

        DepositResponse deposit = DepositSteps.depositToAccount(
                username,
                AdminSteps.getOriginalPassword(username),
                account.getId(),
                RandomData.getDepositAmount()
                );

        softly.assertThat(deposit.getBalance())
                .isBetween(TestDataConstants.MIN_VALUE_DEPOSIT,
                        TestDataConstants.MAX_VALUE_DEPOSIT);
        softly.assertThat(deposit.getId())
                .isEqualTo(account.getId());
        softly.assertThat(deposit.getBalance())
                .isEqualTo(TestDataConstants.DEFAULT_ACCOUNT_BALANCE + deposit.getBalance());


        var accountAfterDeposit = AccountSteps.getAccountById(
                username,
                AdminSteps.getOriginalPassword(username),
                account.getId()
                );

        softly.assertThat(accountAfterDeposit.getBalance()).isEqualTo(deposit.getBalance());
        softly.assertThat(accountAfterDeposit.getTransactions()).isNotNull();
    }

    public static Stream<Arguments> depositInvalidData(){
      return Stream.of(
Arguments.of(RandomData.getInvalidNegativeAmount(), Message.Validation.DEPOSIT_AMOUNT_MIN_0_01),
        Arguments.of(RandomData.getInvalidExceedingAmount(), Message.Validation.DEPOSIT_AMOUNT_MAX_5000),
        Arguments.of(TestDataConstants.ZERO_AMOUNT, Message.Validation.DEPOSIT_AMOUNT_MIN_0_01)
        );
    }

    @MethodSource("depositInvalidData")
    @ParameterizedTest
    @DisplayName("Негатив: пополнение с невалидными данными")
    public void depositWithInvalidDataTest(double invalidAmount, String errorValue){
        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);

        UserCreateAccountResponse account = AccountSteps.createAccount(username,password);
        double defaultBalance = account.getBalance();

        softly.assertThat(defaultBalance).isEqualTo(TestDataConstants.DEFAULT_ACCOUNT_BALANCE);

        DepositSteps.depositWithInvalidAmount(
                username,
                password,
                account.getId(),
                invalidAmount,
                errorValue
        );

        Accounts accountAfterDeposit = AccountSteps.getAccountById(
                username,
                AdminSteps.getOriginalPassword(username),
                account.getId()
        );

        softly.assertThat(accountAfterDeposit.getBalance())
                .isEqualTo(TestDataConstants.DEFAULT_ACCOUNT_BALANCE);
        softly.assertThat(accountAfterDeposit.getTransactions()).isNotNull();
    }

    @Test
    @DisplayName("Негатив: поплнение несуществующего аккаунта")
    public void depositToInvalidAccount(){
        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);

        UserCreateAccountResponse account = AccountSteps.createAccount(username,password);
        double defaultBalance = account.getBalance();

        softly.assertThat(defaultBalance).isEqualTo(TestDataConstants.DEFAULT_ACCOUNT_BALANCE);

        DepositSteps.depositToInvalidAccount(
                username,
                password,
                RandomData.getRandomId(account.getId()),
                RandomData.getDepositAmount(),
                Message.Security.UNAUTHORIZED_ACCESS_TO_ACCOUNT);
    }
}