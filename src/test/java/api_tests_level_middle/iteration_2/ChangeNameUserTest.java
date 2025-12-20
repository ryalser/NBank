package api_tests_level_middle.iteration_2;

import generators.RandomData;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import requests.GetCustomerProfileRequester;
import requests.PutCustomerProfileRequester;
import specs.RequestsSpecs;
import specs.ResponseSpecs;

public class ChangeNameUserTest extends BaseTest {
    @Test
    @DisplayName("Позитивный тест: изменение имени пользователя")
    public void changeNameTest() {
        String username = RandomData.getUsername();
        String password = RandomData.getPassword();
        String newName = RandomData.getName();

        // 1. АДМИН СОЗДАЕТ ПОЛЬЗОВАТЕЛЯ
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(RequestsSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated()).post(createUserRequest);

        // 2. ПОЛЬЗОВАТЕЛЬ ЛОГИНИТСЯ
        RequestSpecification loginUser = RequestsSpecs.authAsUser(username, password);

        // 3. ПОЛЬЗОВАТЕЛЬ МЕНЯЕТ ИМЯ
        PutCustomerProfileRequest putCustomerProfileRequest = PutCustomerProfileRequest.builder()
                .name(newName)
                .build();

        PutCustomerProfileRequester putCustomerProfileRequester = new PutCustomerProfileRequester(loginUser,
                ResponseSpecs.requestReturnsOk());

        PutCustomerProfileResponse putCustomerProfileResponse = putCustomerProfileRequester.put(putCustomerProfileRequest)
                .extract()
                .as(PutCustomerProfileResponse.class);

        // проверяем ответ при изменении имени
        softly.assertThat(putCustomerProfileResponse.getMessage()).isEqualTo("Profile updated successfully");
        softly.assertThat(putCustomerProfileResponse.getCustomer().getName()).isEqualTo(newName);

        // 4. ПОЛЬЗОВАТЕЛЬ ПРОВЕРЯЕТ СВОЙ ПРОФИЛЬ
        GetCustomerProfileResponse getCustomerProfileResponse = new GetCustomerProfileRequester(loginUser,
                ResponseSpecs.requestReturnsOk()).get(null)
                .extract()
                .as(GetCustomerProfileResponse.class);

        // проверяем результат запроса профиля, что действительно изменилось
        softly.assertThat(getCustomerProfileResponse.getUsername()).isEqualTo(username);
        softly.assertThat(getCustomerProfileResponse.getName()).isEqualTo(newName);
        softly.assertThat(getCustomerProfileResponse.getName()).isNotEmpty();
    }
}
