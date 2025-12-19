package api_tests_level_middle.iteration_2;

import generators.RandomData;
import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.*;
import specs.RequestsSpecs;
import specs.ResponseSpecs;

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
}