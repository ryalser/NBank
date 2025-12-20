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

import java.util.Random;
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

        senderAccountId = createAccountRequester.post(null)
                .extract()
                .jsonPath()
                .getInt("id");

        receiverAccountId = createAccountRequester.post(null)
                .extract()
                .jsonPath()
                .getInt("id");

        // 4. ПОЛЬЗОВАТЕЛЬ ПОПОЛНЯЕТ АККАУНТ - senderAccountId
        DepositRequest depositRequest = DepositRequest.builder()
                .id(senderAccountId)
                .balance(100)
                .build();

        new DepositRequester(loginUser,
                ResponseSpecs.requestReturnsOk()).post(depositRequest);

        // 5. ПОЛЬЗОВАТЕЛЬ ПЕРЕВОДИТ ДС
        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(55)
                .build();

        TransferMoneyResponse transferMoneyResponse = new TransferRequester(loginUser,
                ResponseSpecs.requestReturnsOk()).post(transferMoneyRequest)
                .extract()
                .as(TransferMoneyResponse.class);

        softly.assertThat(transferMoneyResponse.getAmount()).isEqualTo(55);
        softly.assertThat(transferMoneyResponse.getMessage()).isEqualTo("Transfer successful");
        softly.assertThat(transferMoneyResponse.getSenderAccountId()).isEqualTo(senderAccountId);
        softly.assertThat(transferMoneyResponse.getReceiverAccountId()).isEqualTo(receiverAccountId);

        // 6. ПОЛЬЗОВАТЕЛЬ ПРОВЕРЯЕТ БАЛАНС ПЕРВОГО И ВТОРОГО АККАУНТА + СМОТРИТ ТРАЗАКЦИЮ
        GetUserAccountsRequester getUserAccountsRequester = new GetUserAccountsRequester(loginUser,
                ResponseSpecs.requestReturnsOk());

        double senderBalance = getUserAccountsRequester.get(null)
                .extract()
                .jsonPath()
                .getDouble("find { it.id == " + senderAccountId + " }.balance");

        double receiverBalance = getUserAccountsRequester.get(null)
                .extract()
                .jsonPath()
                .getDouble("find { it.id == " + receiverAccountId + " }.balance");


        softly.assertThat(senderBalance).isEqualTo(45);
        softly.assertThat(receiverBalance).isEqualTo(55);
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

        senderAccountId = createAccountRequester.post(null)
                .extract()
                .jsonPath()
                .getInt("id");

        receiverAccountId = createAccountRequester.post(null)
                .extract()
                .jsonPath()
                .getInt("id");

        // 4. ПОЛЬЗОВАТЕЛЬ ПОПОЛНЯЕТ АККАУНТ - senderAccountId
        DepositRequest depositRequest = DepositRequest.builder()
                .id(senderAccountId)
                .balance(5000)
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

        double actualBalanceReceiver = getUserAccountsRequester.get(null)
                .extract()
                .jsonPath()
                .getDouble(String.format("find { it.id == %d }.balance", receiverAccountId));

        double actualBalanceSender = getUserAccountsRequester.get(null)
                .extract()
                .jsonPath()
                .getDouble(String.format("find { it.id == %d }.balance", senderAccountId));

        softly.assertThat(actualBalanceSender).isEqualTo(5000.0);
        softly.assertThat(actualBalanceReceiver).isEqualTo(0.0);
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
                .jsonPath()
                .getInt("id");

        // 4. ПОЛЬЗОВАТЕЛЬ ПОПОЛНЯЕТ АККАУНТ - senderAccountId
        DepositRequest depositRequest = DepositRequest.builder()
                .id(senderAccountId)
                .balance(100)
                .build();

        new DepositRequester(loginUser,
                ResponseSpecs.requestReturnsOk()).post(depositRequest);

        // 5. ПОЛЬЗОВАТЕЛЬ ПЕРЕВОДИТ ДС
        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(12345)
                .amount(100)
                .build();

        // Ожидаемый текст в ответе
        String errorValueWithInvalidAccountIdReceiver = "Invalid transfer: insufficient funds or invalid accounts";

        TransferRequester transferRequester = new TransferRequester(loginUser,
                ResponseSpecs.requestReturnsTextBadRequest(errorValueWithInvalidAccountIdReceiver));

        transferRequester.post(transferMoneyRequest);
    }
}
