package api_tests_level_junior.iteration_2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

        String nameUser = kate2006.getNameUser(kate2006.getUserAuthToken());
        assertEquals("New Name", nameUser, "Имя юзера не установлено");
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

        assertNull(kate2007.getNameUser(kate2007.getUserAuthToken()),"Дефолтное значение name не должно меняться.");
    }

    // Негативный
    // Пользователь изменяет имя пустым значением
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

        assertNull(kate2008.getNameUser(kate2008.getUserAuthToken()),"Дефолтное значение name не должно меняться.");
    }
}
