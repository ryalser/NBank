package api_tests_level_junior.iteration_2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DepositAccountTest extends BaseTest {

    // Позитивный
    // Юзер пополняет ДС на свой счет
    @Test
    public void userDepositAccountWithCorrectDataTest(){
        UserAccount user1 = new UserAccount("kate1999","verysTRongPassword34$");
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", user1.getUserAuthToken())
                .body(String.format("""
                                        {
                                           "id": %d,
                                           "balance": 100.5
                                        }
""", user1.getAccountId()))
        .when()
                .post(BASE_URL + "/api/v1/accounts/deposit")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.equalTo(100.5F));

        float balanceUser = user1.getBalanceAccount(user1.getUserAuthToken());
        assertEquals(100.5, balanceUser, "Баланс юзера должен быть: 100.5");
    }

    // Негативный
    // Пополнение неверного аккаунта
    @Test
    public void userDepositAccountWithIncorrectDataTest(){
        UserAccount user2 = new UserAccount("alex1999","AlexPassword34$");
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization",user2.getUserAuthToken())
                .body(String.format("""
                                        {
                                           "id": %d,
                                           "balance": 100.5
                                        }
""", 1234))
        .when()
                .post(BASE_URL + "/api/v1/accounts/deposit")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN);

        float balanceUser = user2.getBalanceAccount(user2.getUserAuthToken());
        assertEquals(0, balanceUser, "Баланс юзера должен быть: 0 т.к. пополнения на неверный аккаунт невозможно");
    }

    // Негативный тест на некорректный JSON
    @Test
    public void userDepositAccountWithIncorrectRequestDataTest(){
        UserAccount user3 = new UserAccount("victor1999","VictorPassword34$");
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization",user3.getUserAuthToken())
                .body(String.format("""
                                        {
                                           "id": %d
                                           "balance": 100.5
                                        }
""", user3.getAccountId()))
      .when()
                .post(BASE_URL + "/api/v1/accounts/deposit")
      .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

        float balanceReceiver = user3.getBalanceAccount(user3.getUserAuthToken());// баланс юзера
        assertEquals(0, balanceReceiver, "Баланс юзера должен остаться: 0");
    }
}




