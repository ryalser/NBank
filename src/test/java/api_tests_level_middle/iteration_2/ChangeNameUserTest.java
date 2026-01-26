package api_tests_level_middle.iteration_2;

import generators.RandomData;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.AdminCreateUserRequester;
import requests.GetCustomerProfileRequester;
import requests.PutCustomerProfileRequester;
import specs.RequestsSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

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
        softly.assertThat(putCustomerProfileResponse.getMessage()).isEqualTo(ResponseSpecs.PROFILE_UPDATED_SUCCESSFULLY);
        softly.assertThat(putCustomerProfileResponse.getCustomer().getName()).isEqualTo(newName);

        // 4. ПОЛЬЗОВАТЕЛЬ ПРОВЕРЯЕТ СВОЙ ПРОФИЛЬ
        GetCustomerProfileResponse getCustomerProfileResponse = new GetCustomerProfileRequester(loginUser,
                ResponseSpecs.requestReturnsOk()).get()
                .extract()
                .as(GetCustomerProfileResponse.class);

        // проверяем результат запроса профиля, что действительно изменилось имя
        softly.assertThat(getCustomerProfileResponse.getUsername()).isEqualTo(username);
        softly.assertThat(getCustomerProfileResponse.getName()).isEqualTo(newName);
    }

    // Набор невалидных "name" для негативных тестов
    public static Stream<Arguments> invalidName() {
        return Stream.of(
                Arguments.of(RandomData.getNameWithoutSpace(),ResponseSpecs.VALIDATION_TWO_WORDS_LETTERS_ONLY),
                Arguments.of(RandomData.getNameWithNumber(),ResponseSpecs.VALIDATION_TWO_WORDS_LETTERS_ONLY),
                Arguments.of(RandomData.getNameWithSpacesOnly(),ResponseSpecs.VALIDATION_TWO_WORDS_LETTERS_ONLY)
        );
    }

    @MethodSource("invalidName")
    @ParameterizedTest
    @DisplayName("Негативный тест: невалидные name")
    public void changeNameWithInvalidName(String name, String errorValue) {
        String username = RandomData.getUsername();
        String password = RandomData.getPassword();

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
                .name(name)
                .build();

        PutCustomerProfileRequester putCustomerProfileRequester = new PutCustomerProfileRequester(loginUser,
                ResponseSpecs.requestReturnsTextBadRequest(errorValue));

        putCustomerProfileRequester.put(putCustomerProfileRequest);

        // 4. ПОЛЬЗОВАТЕЛЬ ПРОВЕРЯЕТ СВОЙ ПРОФИЛЬ
        GetCustomerProfileResponse getCustomerProfileResponse = new GetCustomerProfileRequester(loginUser,
                ResponseSpecs.requestReturnsOk()).get()
                .extract()
                .as(GetCustomerProfileResponse.class);

        softly.assertThat(getCustomerProfileResponse.getName()).isNull(); // должно остаться по дефолту null
    }
}
