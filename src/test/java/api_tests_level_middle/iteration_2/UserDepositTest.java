package api_tests_level_middle.iteration_2;

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

        UserCreateAccountResponse userCreateAccountResponse = createAccountRequester.post(null)
                .extract()
                .as(UserCreateAccountResponse.class);

        int accountId = userCreateAccountResponse.getId();

        // 3. ДЕЛАЕТ ДЕПОЗИТ
        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId)
                .balance(100)
                .build();

        DepositRequester depositRequester = new DepositRequester(
                RequestsSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsOk()
        );

        DepositResponse depositResponse = depositRequester.post(depositRequest)
                .extract()
                .as(DepositResponse.class);

        softly.assertThat(depositResponse.getBalance()).isEqualTo(100);
        softly.assertThat(depositResponse.getAccountNumber()).isEqualTo(accountId);

        // 4. ПРОВЕРКА ЧЕРЕЗ GET МЕТОД /api/v1/customer/accounts
        GetUserAccountsRequester getUserAccountsRequester = new GetUserAccountsRequester(
                RequestsSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsOk()
        );

        double actualBalance = getUserAccountsRequester.get(null)
                .extract()
                .jsonPath()
                .getDouble("[0].balance");

        softly.assertThat(actualBalance).isEqualTo(100);
    }

    // Набор невалидных данных для депозита
    public static Stream<Arguments> depositInvalidData() {
        return Stream.of(
                // Входящее значение, название атрибута, ожидаемая ошибка
                Arguments.of(-10, "Deposit amount must be at least 0.01"),
                        Arguments.of(10500, "Deposit amount cannot exceed 5000"),
                        Arguments.of(0, "Deposit amount must be at least 0.01")
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
        int accountId = createAccountRequester.post(null)
                .extract()
                .jsonPath()
                .getInt("id");

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

        double actualBalance = getUserAccountsRequester.get(null)
                .extract()
                .jsonPath()
                .getDouble("[0].balance");

        softly.assertThat(actualBalance).isEqualTo(0.0);
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

        createAccountRequester.post(null);

        DepositRequest depositRequest = DepositRequest.builder()
                .id(1234)
                .balance(100)
                .build();

        String errorValueWithInvalidAccountId = "Unauthorized access to account";
        // кажется, в апи не совсем корректно реализован этот момент

        DepositRequester depositRequester = new DepositRequester(
                RequestsSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsTextForbidden(errorValueWithInvalidAccountId)
        );

        depositRequester.post(depositRequest);

        GetUserAccountsRequester getUserAccountsRequester = new GetUserAccountsRequester(
                RequestsSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsOk()
        );

        double actualBalance = getUserAccountsRequester.get(null)
                .extract()
                .jsonPath()
                .getDouble("[0].balance");

        softly.assertThat(actualBalance).isEqualTo(0.0);
    }
}