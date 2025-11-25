package api_tests_level_junior.iteration2;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class DepositAccountTest extends BaseTest {

    // Админ создает юзера
    @Test
    public void adminCanCreateUserWithCorrectDataTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", BASIC_AUTHORIZATION_ADMIN)
                .body("""
                            {
                              "username": "kate2003",
                              "password": "Password1234!",
                              "role": "USER"
                            }
""")
                .post(BASE_URL + "/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("username", Matchers.equalTo("kate2003"));
    }

    // Юзер создает себе аккаунт
    @Test
    public void userCreateAccountTest(){


    }
}




