package api_tests_level_middle.iteration_2;

import constants.Message;
import constants.TestConstants;
import generators.RandomData;
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

public class UserDepositTest extends BaseTest {

    @DisplayName("Позитивный тест: пополнение юзером своего аккаунта")
    @Test
    public void userDepositToAccountTest() {
        // 1. СОЗДАЕМ ЮЗЕРА АДМИНОМ
        String username = RandomData.getUsername();
        String password = RandomData.getPassword();

        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role(UserRole.USER.toString())
                .build();

        AdminCreateUserRequester adminRequester = new AdminCreateUserRequester(
                RequestsSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated()
        );

        adminRequester.post(userRequest); // создали пользователя

        // 2. ПОЛЬЗОВАТЕЛЬ СОЗДАЕТ СЕБЕ АККАУНТ
        CreateAccountRequester createAccountRequester = new CreateAccountRequester(
                RequestsSpecs.authAsUser(username, password),
                ResponseSpecs.entityWasCreated()
        );

        UserCreateAccountResponse userCreateAccountResponse = createAccountRequester.post()
                .extract()
                .as(UserCreateAccountResponse.class);

        int accountId = userCreateAccountResponse.getId();

        // 3. ДЕЛАЕТ ДЕПОЗИТ
        double depositAmount = RandomData.getDepositAmount();
        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId)
                .balance(depositAmount)
                .build();

        DepositRequester depositRequester = new DepositRequester(
                RequestsSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsOk()
        );

        DepositResponse depositResponse = depositRequester.post(depositRequest)
                .extract()
                .as(DepositResponse.class);

        softly.assertThat(depositResponse.getBalance()).isEqualTo(depositAmount);

        // 4. ПРОВЕРКА ЧЕРЕЗ GET МЕТОД /api/v1/customer/accounts
        GetUserAccountsRequester getUserAccountsRequester = new GetUserAccountsRequester(
                RequestsSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsOk()
        );

        List<Accounts> accounts = Arrays.asList(
                getUserAccountsRequester.get()
                        .extract()
                        .as(Accounts[].class)
        );

        Accounts accounts1 = accounts.stream()
                .filter(account -> account.getId() == accountId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(Message.Business.ACCOUNT_NOT_FOUND));

        softly.assertThat(accounts1.getBalance()).isEqualTo(depositAmount);
    }

    // Набор невалидных данных для депозита
    public static Stream<Arguments> depositInvalidData() {
        return Stream.of(
                // Входящее значение, название атрибута, ожидаемая ошибка
                Arguments.of(RandomData.getInvalidNegativeAmount(), Message.Validation.DEPOSIT_AMOUNT_MIN_0_01),
                        Arguments.of(RandomData.getInvalidExceedingAmount(), Message.Validation.DEPOSIT_AMOUNT_MAX_5000),
                        Arguments.of(TestConstants.ZERO_AMOUNT, Message.Validation.DEPOSIT_AMOUNT_MIN_0_01)
        );
    }

    @MethodSource("depositInvalidData")
    @ParameterizedTest
    @DisplayName("Негативный тест: пополнение с невалидными данными")
    public void depositWithInvalidDataTest(double amountDeposit, String errorValue) {
        // 1. СОЗДАЕМ ЮЗЕРА АДМИНОМ
        String username = RandomData.getUsername();
        String password = RandomData.getPassword();

        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role(UserRole.USER.toString())
                .build();

        AdminCreateUserRequester adminRequester = new AdminCreateUserRequester(
                RequestsSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated()
        );

        adminRequester.post(userRequest); // создали пользователя

        // 2. ПОЛЬЗОВАТЕЛЬ СОЗДАЕТ СЕБЕ АККАУНТ
        CreateAccountRequester createAccountRequester = new CreateAccountRequester(
                RequestsSpecs.authAsUser(username, password),
                ResponseSpecs.entityWasCreated()
        );

        // Получить id аккаунта
        UserCreateAccountResponse userCreateAccountResponse = createAccountRequester.post()
                .extract()
                .as(UserCreateAccountResponse.class);

        int accountId = userCreateAccountResponse.getId();
        double initialBalance = userCreateAccountResponse.getBalance();

        // 3. ДЕЛАЕТ ДЕПОЗИТ С НЕВАЛИДНЫМИ ДАННЫМИ
        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId)
                .balance(amountDeposit)
                .build();

        DepositRequester depositRequester = new DepositRequester(
                RequestsSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsTextBadRequest(errorValue)
        );

        depositRequester.post(depositRequest);

        // 4. ПРОВЕРКА ЧЕРЕЗ GET МЕТОД /api/v1/customer/accounts
        GetUserAccountsRequester getUserAccountsRequester = new GetUserAccountsRequester(
                RequestsSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsOk()
        );

        Accounts[] accountsArray = getUserAccountsRequester.get()
                .extract()
                .as(Accounts[].class);

        List<Accounts> accounts = Arrays.asList(accountsArray);

        Accounts accounts1 = accounts.stream()
                .filter(account -> account.getId() == accountId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(Message.Business.ACCOUNT_NOT_FOUND));

        softly.assertThat(accounts1.getBalance()).isEqualTo(initialBalance);
    }

    @Test
    @DisplayName("Негативный тест: поплнение несуществующего аккаунта")
    public void depositUserInvalidAccountTest() {
        String username = RandomData.getUsername();
        String password = RandomData.getPassword();

        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role(UserRole.USER.toString())
                .build();

        AdminCreateUserRequester adminCreateUserRequester = new AdminCreateUserRequester(
                RequestsSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated()
        );

        adminCreateUserRequester.post(createUserRequest);

        CreateAccountRequester createAccountRequester = new CreateAccountRequester(
             RequestsSpecs.authAsUser(username, password),
             ResponseSpecs.entityWasCreated()
        );

        UserCreateAccountResponse userCreateAccountResponse = createAccountRequester.post()
                .extract()
                .as(UserCreateAccountResponse.class);

        int accountId = userCreateAccountResponse.getId();
        double initialBalance = userCreateAccountResponse.getBalance();

        DepositRequest depositRequest = DepositRequest.builder()
                .id(RandomData.getRandomId(accountId))
                .balance(RandomData.getDepositAmount())
                .build();
        // кажется, в апи не совсем корректно реализован этот момент

        DepositRequester depositRequester = new DepositRequester(
                RequestsSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsTextForbidden(Message.Security.UNAUTHORIZED_ACCESS_TO_ACCOUNT)
        );
        depositRequester.post(depositRequest);

        GetUserAccountsRequester getUserAccountsRequester = new GetUserAccountsRequester(
                RequestsSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsOk()
        );

        Accounts[] userAccounts = getUserAccountsRequester.get()
                .extract()
                .as(Accounts[].class);

        // Проверяем, что есть созданный аккаунт
        softly.assertThat(userAccounts).isNotEmpty();

        // Находим созданный аккаунт по ID
        Accounts createdAccount = Arrays.stream(userAccounts)
                .filter(account -> account.getId() == accountId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(Message.Business.ACCOUNT_NOT_FOUND));

        softly.assertThat(createdAccount.getBalance()).isEqualTo(initialBalance);
    }
}