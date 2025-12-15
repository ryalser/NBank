package iteration_2;

import generators.RandomData;
import iteration_1.CreateUserTest;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.UserRole;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import specs.RequestsSpecs;
import specs.ResponseSpecs;

public class UserDepositTest extends BaseTest {

    @Test
    public void userDepositToAccountTest() {
        // Генерация тестовых данных, вывел для наглядности перед созданием объекта
        String username = RandomData.getUsername();
        String password = RandomData.getPassword();

        // Создание запроса(объекта) на создание юзера
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(username) // рандомный username
                .password(password) // рандомный password
                .role(UserRole.USER.toString()) // роль обычного юзера, приведенная к строке
                .build();

        // Создается реквестер(объект для выполнения запроса) для админа
        // Он знает:
        // - куда отправлять запрос (спецификация админа)
        // - какой ответ ожидать (спецификация "сущность создана" - статус 201)
        AdminCreateUserRequester adminRequester = new AdminCreateUserRequester(
                RequestsSpecs.adminSpec(), // спецификация админа
                ResponseSpecs.entityWasCreated() // ожидание статуса 201 Created
        );

        CreateUserResponse userResponse = adminRequester.post(userRequest)
                .extract()
                .as(CreateUserResponse.class);
    }
}
