package iteration_1;

import generators.RandomData;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.UserRole;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import specs.RequestsSpecs;
import specs.ResponseSpecs;

public class CreateUserTest extends BaseTest {

    // ГЕНЕРАЦИЯ ТЕСТОВЫХ ДАННЫх
    @Test
    public void adminCanCreateUserWithCorrectData() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())  // Генерация username
                .password(RandomData.getPassword())  // Генерация password
                .role(UserRole.USER.toString()) // Установка роли
                .build();

        // Сборка объекта
        /*
        {
            "username": "AbCdEfGhIj",
            "password": "ABCdefgh123$",
            "role": "USER"
        }
         */

        // СОЗДАНИЕ ПОЛЬЗОВАТЕЛЯ (Админом)
        CreateUserResponse createUserResponse = new AdminCreateUserRequester(
                RequestsSpecs.adminSpec(), // Настройка запроса - передаем спецификацию админа
                ResponseSpecs.entityWasCreated()) // Настрока ожидаемого ответа (201 статус)
                .post(createUserRequest).extract().as(CreateUserResponse.class);

        softly.assertThat(createUserRequest.getUsername()).isEqualTo(createUserResponse.getUsername());
        softly.assertThat(createUserRequest.getRole()).isEqualTo(createUserResponse.getRole());
        }
    }

