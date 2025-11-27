package api_tests_level_junior.iteration2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class CreateUserTest extends BaseTest {
    // Админ создает юзера
    @Test
    public void adminCanCreateUserWithAccount() {
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
        .when()
                .post(BASE_URL + "/api/v1/admin/users")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("username", Matchers.equalTo("kate2003"));

        // Юзер получает токен
        String user_auth_token = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                            {
                              "username": "kate2003",
                              "password": "Password1234!"
                            }
""")
        .when()
                .post(BASE_URL + "/api/v1/auth/login")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .header("Authorization",Matchers.notNullValue())
                .extract()
                .header("Authorization");

        // Юзер создает себе аккаунт
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", user_auth_token)
        .when()
                .post(BASE_URL + "/api/v1/accounts")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("id", Matchers.notNullValue())
                .body("accountNumber", Matchers.notNullValue())
                .body("balance", Matchers.equalTo(0.0F));
    }
}
