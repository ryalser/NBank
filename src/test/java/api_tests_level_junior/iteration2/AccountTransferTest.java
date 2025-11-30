package api_tests_level_junior.iteration2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class AccountTransferTest extends BaseTest {
    // Тест перевода ДС между аккаунтами
    @Test
    public void userTransfersMoneyToAnotherAccounts(){
        UserAccount userSenders = new UserAccount("michail1999","MichailPassword34$");
        UserAccount userReceiver = new UserAccount("pavel1999","PavelTRongPassword34$");

        // Отправитель пополняет свой счет
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization",userSenders.getUserAuthToken())
                .body(String.format("""
                                        {
                                           "id": %d,
                                           "balance": 100.5
                                        }
""", userSenders.getAccountId()))
        .when()
                .post(BASE_URL + "/api/v1/accounts/deposit")
        .then()
                .assertThat()
                .statusCode(200);

        // Отправитель переводит ДС на счет получателя
        given()
                 .contentType(ContentType.JSON)
                 .accept(ContentType.JSON)
                .header("Authorization",userSenders.getUserAuthToken())
                .body(String.format("""
                                        {
                                           "senderAccountId": %d,
                                           "receiverAccountId": %d,
                                           "amount": 50.5
                                        }
""", userSenders.getAccountId(), userReceiver.getAccountId()))
        .when()
                .post(BASE_URL + "/api/v1/accounts/transfer")
        .then()
                .assertThat()
                .statusCode(200)
                .body("message", Matchers.equalTo("Transfer successful"));

        // Получатель проверяет свой вккаунт и баланс
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization",userReceiver.getUserAuthToken())
        .when()
                .get(BASE_URL + "/api/v1/customer/accounts")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].id", Matchers.equalTo(userReceiver.getAccountId()))
                .body("[0].balance", Matchers.equalTo(50.5F));

    }
}
