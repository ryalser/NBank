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
import requests.steps.TransferSteps;

import java.util.List;
import java.util.stream.Stream;

public class TransferTest extends BaseTest {
    @Test
    @DisplayName("Успешный перевод между аккаунтами")
    public void successfulMoneyTransfer(){
        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);

        List<UserCreateAccountResponse> accounts = AccountSteps.createTwoAccounts(username,password);
        int senderAccountId = accounts.get(0).getId();
        int receiverAccountId = accounts.get(1).getId();

        DepositResponse depositResponse = DepositSteps.depositToAccount(
                username,password, senderAccountId, RandomData.getDepositAmount());
        double depositAmount = depositResponse.getBalance();

        TransferMoneyResponse transfer = TransferSteps.transferBetweenAccounts(
                username,password,senderAccountId,receiverAccountId,depositAmount);

        softly.assertThat(transfer.getAmount()).isEqualTo(depositAmount);
        softly.assertThat(AccountSteps.getBalanceAccount(username,password,senderAccountId))
                .isEqualTo(TestDataConstants.DEFAULT_ACCOUNT_BALANCE);
        softly.assertThat(AccountSteps.getBalanceAccount(username,password,receiverAccountId))
                .isEqualTo(TestDataConstants.DEFAULT_ACCOUNT_BALANCE + depositAmount);
    }

    public static Stream<Arguments> transferInvalidData(){
        return Stream.of(
                Arguments.of(RandomData.getInvalidNegativeAmount(),
                        Message.Validation.AMOUNT_TRANSFER_MIN_0_01),
                Arguments.of(RandomData.getInvalidExceedingAmount(),
                        Message.Validation.AMOUNT_TRANSFER_MAX_10000),
                Arguments.of(TestDataConstants.ZERO_AMOUNT,
                        Message.Validation.AMOUNT_TRANSFER_MIN_0_01)
        );
    }

    @MethodSource("transferInvalidData")
    @ParameterizedTest
    @DisplayName("Негатив: перевод с невалидными данными")
    public void transferMoneyWithInvalidDataTest(double invalidAmount, String errorValue){
        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);

        List<UserCreateAccountResponse> accounts = AccountSteps.createTwoAccounts(username,password);
        int senderAccountId = accounts.get(0).getId();
        int receiverAccountId = accounts.get(1).getId();

        DepositResponse depositResponse = DepositSteps.depositToAccount(
                username,password, senderAccountId, RandomData.getDepositAmount());
        double depositAmount = depositResponse.getBalance();

        TransferSteps.transferWithInvalidAmount(
                username,
                password,
                senderAccountId,
                receiverAccountId,
                invalidAmount,
                errorValue
        );

        softly.assertThat(AccountSteps.getBalanceAccount(username,password,senderAccountId))
                .isEqualTo(depositAmount);
        softly.assertThat(AccountSteps.getBalanceAccount(username,password,receiverAccountId))
                .isEqualTo(TestDataConstants.DEFAULT_ACCOUNT_BALANCE);
    }

    @Test
    @DisplayName("Негатив: перевод на несуществующий аккаунт")
    public void transferMoneyWithInvalidAccount(){
        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);

        int senderAccountId = AccountSteps.createAccount(username,password).getId();

        DepositResponse depositResponse = DepositSteps.depositToAccount(
                username,password, senderAccountId, RandomData.getDepositAmount());
        double depositAmount = depositResponse.getBalance();

        TransferSteps.transferToInvalidAccount(
                username,
                password,
                senderAccountId,
                RandomData.getRandomId(senderAccountId),
                depositAmount,
                Message.Validation.INVALID_ACCOUNT_OR_INSUFFICIENT_FUNDS
        );

        softly.assertThat(AccountSteps.getBalanceAccount(username,password,senderAccountId))
                .isEqualTo(depositAmount);
        softly.assertThat(AccountSteps.getBalanceAccount(username,password, senderAccountId))
                .isNotEqualTo(TestDataConstants.DEFAULT_ACCOUNT_BALANCE);
    }
}