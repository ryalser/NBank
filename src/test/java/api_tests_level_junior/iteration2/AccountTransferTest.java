package api_tests_level_junior.iteration2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTransferTest extends BaseTest {
    // Позитивный
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
                                           "balance": 100.55
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

        float balanceReceiver = userReceiver.getBalanceAccount(userReceiver.getUserAuthToken());// баланс получателя
        float balanceSender = userSenders.getBalanceAccount(userSenders.getUserAuthToken()); // баланс отправителя

        assertEquals(50.5, balanceReceiver, "Баланс получателя должен быть: 50.5");
        assertEquals(50.05, balanceSender, 0.01F, "Баланс отправителя должен быть уменьшен до: 50.05");
    }

    // Негативный
    // Перевод суммы превышающей баланс юзера
    @Test
    public void userTransfersMoneyToAnotherAccountsButNotEnoughFunds(){
        UserAccount userSender = new UserAccount("oleg1999","olegPassword34$");
        UserAccount userReceiver = new UserAccount("dima1999","dimaPassword34$");

        // Юзер пополняет баланс
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization",userSender.getUserAuthToken())
                .body(String.format("""
                                        {
                                           "id": %d,
                                           "balance": 100.55
                                        }
""", userSender.getAccountId()))
         .when()
                .post(BASE_URL + "/api/v1/accounts/deposit")
         .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        // Отправитель переводит ДС на счет получателя, но средст НЕДОСТАТОЧНО
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization",userSender.getUserAuthToken())
                .body(String.format("""
                                        {
                                           "senderAccountId": %d,
                                           "receiverAccountId": %d,
                                           "amount": 5000.5
                                        }
""", userSender.getAccountId(), userReceiver.getAccountId()))
        .when()
                .post(BASE_URL + "/api/v1/accounts/transfer")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo("Invalid transfer: insufficient funds or invalid accounts"));

        float balanceReceiver = userReceiver.getBalanceAccount(userReceiver.getUserAuthToken());// баланс получателя
        float balanceSender = userSender.getBalanceAccount(userSender.getUserAuthToken()); // баланс отправителя

        assertEquals(0, balanceReceiver, "Баланс получателя должен остаться: 0");
        assertEquals(100.55, balanceSender, 0.01F, "Баланс отправителя должен остаться: 100.55");
    }

    // Негативный
    // Перевод отрицательной суммы
    @Test
    public void userTransfersMoneyWithNegativeAmountShouldFail(){
        UserAccount userSender = new UserAccount("marina1999","marinaPassword34$");
        UserAccount userReceiver = new UserAccount("olga1999","olgaPassword34$");

        // Юзер пополняет баланс
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization",userSender.getUserAuthToken())
                .body(String.format("""
                                        {
                                           "id": %d,
                                           "balance": 100.55
                                        }
""", userSender.getAccountId()))
                .when()
                .post(BASE_URL + "/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        // Отправитель переводит ДС на счет получателя, но средст НЕДОСТАТОЧНО
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization",userSender.getUserAuthToken())
                .body(String.format("""
                                        {
                                           "senderAccountId": %d,
                                           "receiverAccountId": %d,
                                           "amount": -5000.5
                                        }
""", userSender.getAccountId(), userReceiver.getAccountId()))
                .when()
                .post(BASE_URL + "/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo("Transfer amount must be at least 0.01"));

        float balanceReceiver = userReceiver.getBalanceAccount(userReceiver.getUserAuthToken());// баланс получателя
        float balanceSender = userSender.getBalanceAccount(userSender.getUserAuthToken()); // баланс отправителя

        assertEquals(0, balanceReceiver, "Баланс получателя должен остаться: 0");
        assertEquals(100.55, balanceSender, 0.01F, "Баланс отправителя должен остаться: 100.55");
    }
}
