package api_tests_level_junior.iteration2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class DepositAccountTest extends BaseTest {
    // Юзер пополняет ДС на свой счет
    @Disabled
    @Test
    public void userDepositAccountTest(){
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic a2F0ZTIwMDM6UGFzc3dvcmQxMjM0IQ==")
                .body("""
                            {
                               "id": 1,
                               "balance": 100.5
}
""")
                .post(BASE_URL + "/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.equalTo("100.5"));
    }
}




