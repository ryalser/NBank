package api_tests_level_junior.iteration2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class UserChangesUsernameTest extends BaseTest {

    // Позитивный
    // Пользователь изменяет имя
    @Test
    public void userChangesUsernameTest(){
        UserAccount kate2006 = new UserAccount("Kate2006","MichailPassword34$");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", kate2006.getUserAuthToken())
                .body("""
                            {
                              "name": "New Name"
                            }
""")
       .when()
                .put(BASE_URL + "/api/v1/customer/profile")
       .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("customer.name", Matchers.equalTo("New Name"));
    }

    // Негативный
    // Пользователь изменяет имя и забывает пробел
    @Test
    public void userChangesUsernameWithoutSpaceTest(){
        UserAccount kate2007 = new UserAccount("Kate2007","MichailPassword34$");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", kate2007.getUserAuthToken())
                .body("""
                            {
                              "name": "NewName"
                            }
""")
                .when()
                .put(BASE_URL + "/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo("Name must contain two words with letters only"));
    }

    // Негативный
    // Пользователь изменяет имя и забывает пробел
    @Test
    public void userChangesUsernameWithEmptyValueTest(){
        UserAccount kate2008 = new UserAccount("Kate2008","MichailPassword34$");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", kate2008.getUserAuthToken())
                .body("""
                            {
                              "name": ""
                            }
""")
                .when()
                .put(BASE_URL + "/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo("Name must contain two words with letters only"));
    }
}
