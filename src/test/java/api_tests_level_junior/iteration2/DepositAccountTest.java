package api_tests_level_junior.iteration2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class DepositAccountTest extends BaseTest {

    //CreateUserTest  user_1 = new CreateUserTest();

    // Юзер пополняет ДС на свой счет
    @Test
    public void userDepositAccountWithCorrectDataTest(){
       // user_1.adminCanCreateUserWithAccount();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", userAuthToken)
                .body(String.format("""
    {
        "id": %d,
        "balance": 100.5
    }
""", accountId))
        .when()
                .post(BASE_URL + "/api/v1/accounts/deposit")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.equalTo(100.5F));
    }
}




