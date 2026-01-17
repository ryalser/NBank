package api_tests_level_middle.iteration_2;

import generators.RandomData;
import io.restassured.specification.RequestSpecification;
import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.*;
import specs.RequestsSpecs;
import specs.ResponseSpecs;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class TransferMoneyTest extends BaseTest {

    @Test
    @DisplayName("Позитивный тест: успешный перевод между аккаунтами")
    public void transferMoneyBetweenAccountsTest() {
        String username = RandomData.getUsername();
        String password = RandomData.getPassword();
        int senderAccountId;
        int receiverAccountId;

        // 1. АДМИН СОЗДАЕТ ПОЛЬЗОВАТЕЛЯ
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(RequestsSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated()).post(createUserRequest);

        // 2. ПОЛЬЗОВАТЕЛЬ ЛОГИНИТСЯ - БУДУ ПЕРЕИСПОЛЬЗОВАТЬ
        RequestSpecification loginUser = RequestsSpecs.authAsUser(username, password);

        // 3. ПОЛЬЗОВАТЕЛЬ СОЗДАЕТ ДВА АККАУНТА
        CreateAccountRequester createAccountRequester = new CreateAccountRequester(loginUser,
                ResponseSpecs.entityWasCreated());

        UserCreateAccountResponse senderAccount = createAccountRequester.post(null)
                .extract()
                .as(UserCreateAccountResponse.class);

        UserCreateAccountResponse receiverAccount = createAccountRequester.post(null)
                .extract()
                .as(UserCreateAccountResponse.class);

        senderAccountId = senderAccount.getId();
        receiverAccountId = receiverAccount.getId();

        // 4. ПОЛЬЗОВАТЕЛЬ ПОПОЛНЯЕТ АККАУНТ - senderAccountId
        double depositAmount = RandomData.getDepositAmount();
        DepositRequest depositRequest = DepositRequest.builder()
                .id(senderAccountId)
                .balance(depositAmount)
                .build();

        new DepositRequester(loginUser,
                ResponseSpecs.requestReturnsOk()).post(depositRequest);

        // 5. ПОЛЬЗОВАТЕЛЬ ПЕРЕВОДИТ ДС
        double transferAmount = RandomData.getTransferAmount(depositAmount);

        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(transferAmount)
                .build();

        TransferMoneyResponse transferMoneyResponse = new TransferRequester(loginUser,
                ResponseSpecs.requestReturnsOk()).post(transferMoneyRequest)
                .extract()
                .as(TransferMoneyResponse.class);

        softly.assertThat(transferMoneyResponse.getAmount()).isEqualTo(transferAmount);
        softly.assertThat(transferMoneyResponse.getMessage()).isEqualTo("Transfer successful");
        softly.assertThat(transferMoneyResponse.getSenderAccountId()).isEqualTo(senderAccountId);
        softly.assertThat(transferMoneyResponse.getReceiverAccountId()).isEqualTo(receiverAccountId);

        // 6. ПОЛЬЗОВАТЕЛЬ ПРОВЕРЯЕТ БАЛАНС ПЕРВОГО И ВТОРОГО АККАУНТА
        GetUserAccountsRequester getUserAccountsRequester = new GetUserAccountsRequester(loginUser,
                ResponseSpecs.requestReturnsOk());

        List<Accounts> accounts = Arrays.asList(
                getUserAccountsRequester.get(null)
                        .extract()
                        .as(Accounts[].class)
        );

        Accounts senderAccountAfterTransfer = accounts.stream()
                .filter(account -> account.getId() == senderAccountId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("аккаунт не найден"));

        Accounts receiverAccountAfterTransfer = accounts.stream()
                .filter(account -> account.getId() == receiverAccountId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("аккаунт не найден"));

        softly.assertThat(senderAccountAfterTransfer.getBalance()).isEqualTo(depositAmount - transferAmount);
        softly.assertThat(receiverAccountAfterTransfer.getBalance()).isEqualTo(transferAmount);
    }

    // НАБОР НЕВАЛИДНЫХ ДАННЫХ ДЛЯ ТРАНСФЕРА
    public static Stream<Arguments> transferInvalidData() {
        return Stream.of(
                Arguments.of(-1,"Transfer amount must be at least 0.01"),
                Arguments.of(10001,"Transfer amount cannot exceed 10000"),
                Arguments.of(5001,"Invalid transfer: insufficient funds or invalid accounts")
        );
    }

    @MethodSource("transferInvalidData")
    @ParameterizedTest
    @DisplayName("Негативный тест: перевод с невалидными суммами")
    public void transferMoneyWithInvalidAmountTest(double amount, String errorValue) {
        String username = RandomData.getUsername();
        String password = RandomData.getPassword();
        int senderAccountId;
        int receiverAccountId;

        // 1. АДМИН СОЗДАЕТ ПОЛЬЗОВАТЕЛЯ
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(RequestsSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated()).post(createUserRequest);

        // 2. ПОЛЬЗОВАТЕЛЬ ЛОГИНИТСЯ - БУДУ ПЕРЕИСПОЛЬЗОВАТЬ
        RequestSpecification loginUser = RequestsSpecs.authAsUser(username, password);

        // 3. ПОЛЬЗОВАТЕЛЬ СОЗДАЕТ ДВА АККАУНТА
        CreateAccountRequester createAccountRequester = new CreateAccountRequester(loginUser,
                ResponseSpecs.entityWasCreated());

        UserCreateAccountResponse senderAccount = createAccountRequester.post(null)
                .extract()
                .as(UserCreateAccountResponse.class);

        senderAccountId = senderAccount.getId();

        UserCreateAccountResponse receiverAccount = createAccountRequester.post(null)
                .extract()
                .as(UserCreateAccountResponse.class);

        receiverAccountId = receiverAccount.getId();

        // 4. ПОЛЬЗОВАТЕЛЬ ПОПОЛНЯЕТ АККАУНТ - senderAccountId
        double depositAmount = RandomData.getDepositAmount();
        DepositRequest depositRequest = DepositRequest.builder()
                .id(senderAccountId)
                .balance(depositAmount)
                .build();

        new DepositRequester(loginUser,
                ResponseSpecs.requestReturnsOk()).post(depositRequest);

        // 5. ПОЛЬЗОВАТЕЛЬ ПЕРЕВОДИТ ДС C НЕВАЛИДНЫМИ СУММАМИ
        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(amount)
                .build();

        TransferRequester transferRequester = new TransferRequester(loginUser,
                ResponseSpecs.requestReturnsTextBadRequest(errorValue));

        transferRequester.post(transferMoneyRequest);

        // 6. ПОЛЬЗОВАТЕЛЬ ПРОВЕРЯЕТ БАЛАНС ПЕРВОГО И ВТОРОГО АККАУНТА + СМОТРИТ ЧТО БАЛАНС НЕ ПОМЕНЯЛСЯ
        GetUserAccountsRequester getUserAccountsRequester = new GetUserAccountsRequester(loginUser,
                ResponseSpecs.requestReturnsOk());

        List<Accounts> accounts = Arrays.asList(
                getUserAccountsRequester.get(null)
                        .extract()
                        .as(Accounts[].class)
        );

        Accounts senderAccountAfterTransfer = accounts.stream()
                .filter(account -> account.getId() == senderAccountId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("аккаунт не найден"));

        Accounts receiverAccountAfterTransfer = accounts.stream()
                .filter(account -> account.getId() == receiverAccountId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("аккаунт не найден"));

        softly.assertThat(senderAccountAfterTransfer.getBalance()).isEqualTo(depositAmount);
        softly.assertThat(receiverAccountAfterTransfer.getBalance()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Негативный тест: указан несуществующий аккаунт")
    public void transferMoneyWithInvalidAccountTest() {
        String username = RandomData.getUsername();
        String password = RandomData.getPassword();
        int senderAccountId;

        // 1. АДМИН СОЗДАЕТ ПОЛЬЗОВАТЕЛЯ
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(RequestsSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated()).post(createUserRequest);

        // 2. ПОЛЬЗОВАТЕЛЬ ЛОГИНИТСЯ - БУДУ ПЕРЕИСПОЛЬЗОВАТЬ
        RequestSpecification loginUser = RequestsSpecs.authAsUser(username, password);

        // 3. ПОЛЬЗОВАТЕЛЬ СОЗДАЕТ АКК
        CreateAccountRequester createAccountRequester = new CreateAccountRequester(loginUser,
                ResponseSpecs.entityWasCreated());

        senderAccountId = createAccountRequester.post(null)
                .extract()
                .as(UserCreateAccountResponse.class)
                .getId();

        // 4. ПОЛЬЗОВАТЕЛЬ ПОПОЛНЯЕТ АККАУНТ - senderAccountId
        double depositAmount = RandomData.getDepositAmount();
        DepositRequest depositRequest = DepositRequest.builder()
                .id(senderAccountId)
                .balance(depositAmount)
                .build();

        new DepositRequester(loginUser,
                ResponseSpecs.requestReturnsOk()).post(depositRequest);

        // 5. ПОЛЬЗОВАТЕЛЬ ПЕРЕВОДИТ ДС
        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(RandomData.getRandomId())
                .amount(RandomData.getTransferAmount(depositAmount))
                .build();

        // Ожидаемый текст в ответе
        String errorValueWithInvalidAccountIdReceiver = "Invalid transfer: insufficient funds or invalid accounts";

        TransferRequester transferRequester = new TransferRequester(loginUser,
                ResponseSpecs.requestReturnsTextBadRequest(errorValueWithInvalidAccountIdReceiver));

        transferRequester.post(transferMoneyRequest);
    }
}
