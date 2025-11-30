package api_tests_level_junior.iteration2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

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
    }
}




