package iteration_2;

import generators.RandomData;
import iteration_1.CreateUserTest;
import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import requests.CreateAccountRequester;
import requests.DepositRequester;
import requests.LoginUserRequester;
import specs.RequestsSpecs;
import specs.ResponseSpecs;

public class UserDepositTest extends BaseTest {

    @DisplayName("Позитивный тест: пополнение юзером своего аккаунта")
    @Test
    public void userDepositToAccountTest() {
        // 1. СОЗДАЕМ ЮЗЕРА АДМИНОМ
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

        // Что у нас сейчас в userResponse:
        // userResponse.getId() - ID созданного пользователя (нужен для дальнейших операций)
        // serResponse.getUsername() - имя пользователя (совпадает с тем, что отправили)
        // userResponse.getPassword() - пароль (обычно возвращается хэшированный)
        CreateUserResponse userResponse = adminRequester.post(userRequest)
                .extract()
                .as(CreateUserResponse.class);

        // 2. ЛОГИНИМ ПОЛЬЗОВАТЕЛЯ
        // Создаем модель отправки
        LoginUserRequest loginRequest = LoginUserRequest.builder()
                .username(username)
                .password(password)
                .build();

        // Создаем ревестер - объект для выполнения запроса
        // Без авторизации т.к юзер еще не авторизован
        LoginUserRequester loginRequester = new LoginUserRequester(
                RequestsSpecs.unAuthSpec(),
                ResponseSpecs.requestReturnsOk()
        );

        // Выполняется запрос на логин
        LoginUserResponse loginUserResponse = loginRequester.post(loginRequest)
                .extract()
                .as(LoginUserResponse.class);

        // Запишем токен в переменную
        String authToken = loginRequester.post(loginRequest)
                .extract()
                .header("Authorization");

        System.out.println("Получили токен юзера: " + authToken);
        // Теперь у нас есть:
        // - userResponse - данные созданного пользователя
        // - loginResponse - данные из ответа на логин
        // - authToken - токен для авторизации в следующих запросах

        // 3. ПОЛЬЗОВАТЕЛЬ СОЗДАЕТ СЕБЕ АККАУНТ
        CreateAccountRequester createAccountRequester = new CreateAccountRequester(
                RequestsSpecs.authAsUser(username, password),
                ResponseSpecs.entityWasCreated()
        );

        UserCreateAccountResponse userCreateAccountResponse = createAccountRequester.post(null) // Запрос без BODY
                .extract()
                .as(UserCreateAccountResponse.class);
            // Теперь у нас есть accountResponse с данными созданного аккаунта:
            // - accountResponse.getId() - ID аккаунта
            // - accountResponse.getAccountNumber() - номер счета
            // - accountResponse.getBalance() - баланс (скорее всего 0.0)

        // Запишем полученные данные пользователя
        int accountId = userCreateAccountResponse.getId();
        double balanceBefore = userCreateAccountResponse.getBalance();

        // 4. ДЕЛАЕМ ДЕПОЗИТ
        // Готовим запрос
        DepositRequest depositRequest = DepositRequest.builder()
                .id(userCreateAccountResponse.getId())
                .balance(100)
                .build();

        DepositRequester depositRequester = new DepositRequester(
                RequestsSpecs.authAsUser(username,password), // пользователь авторизуется
                ResponseSpecs.requestReturnsOk() // ожидаем статус 200
        );

        // Собираем Response
        DepositResponse depositResponse = depositRequester.post(depositRequest)
                .extract()
                .as(DepositResponse.class);

        // 5. ПРОВЕРКА АССЕРТАМИ + ВЫЗОВ ГЕТ МЕТОДА ДЛЯ ПРОВЕРКИ
    }
}