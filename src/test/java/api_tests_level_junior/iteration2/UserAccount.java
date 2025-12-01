package api_tests_level_junior.iteration2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;

import java.util.List;

import static io.restassured.RestAssured.given;


public class UserAccount extends BaseTest {
    private final String userAuthToken;
    private final int accountId;
    private final String accountNumber;

    // Конструктор класса - админом создается юзер с аккаунтом - это мои базовые тестовые данные для всех тестов
    public UserAccount(String username, String password) {
        // Создаем пользователя
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", BASIC_AUTHORIZATION_ADMIN)
                .body(String.format("""
                    {"username": "%s", "password": "%s", "role": "USER"}""", username, password))
                .post(BASE_URL + "/api/v1/admin/users")
                .then()
                .statusCode(HttpStatus.SC_CREATED);

        // Получаем токен
        this.userAuthToken = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                    {"username": "%s", "password": "%s"}""", username, password))
                .post(BASE_URL + "/api/v1/auth/login")
                .then()
                .extract()
                .header("Authorization");

        // Создаем аккаунт
        io.restassured.response.Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", this.userAuthToken)
                .post(BASE_URL + "/api/v1/accounts")
                .then()
                .extract()
                .response();

        this.accountId = response.path("id");
        this.accountNumber = response.path("accountNumber");

        System.out.println("Создан пользователь: " + username);
    }

    // Геттеры для получения тестовых данных юзера в тестовых классах
    public String getUserAuthToken() {
        return userAuthToken;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    // Удалить всех юзеров
    public static void cleanUsersData(){
        List<Integer> usersId = given()
                .header("Authorization", BASIC_AUTHORIZATION_ADMIN)
                .get(BASE_URL + "/api/v1/admin/users")
                .then()
                .extract()
                .jsonPath()
                .getList("id");

        for(Integer id : usersId){
            if(id == null){
                System.out.println("Список юзеров пуст.");
            }
            given()
                    .header("Authorization", BASIC_AUTHORIZATION_ADMIN)
                    .delete(BASE_URL + "/api/v1/admin/users/" + id);
        }
    }

    // Получить баланс
    public float getBalanceAccount(String userAuthToken){
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization",userAuthToken)
               .when()
                .get(BASE_URL + "/api/v1/customer/accounts")
               .then()
                .extract()
                .jsonPath()
                .getFloat("[0].balance"); // смотрим баланс первого аккаунта
    }

    // Получить name юзера
    public String getNameUser(String userAuthToken){
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization",userAuthToken)
               .when()
                .get(BASE_URL + "/api/v1/customer/profile")
               .then()
                .extract()
                .jsonPath()
                .getString("name");
    }
}