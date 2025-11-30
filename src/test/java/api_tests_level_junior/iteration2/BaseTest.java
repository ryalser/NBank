package api_tests_level_junior.iteration2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;

public class BaseTest {
    public static final String BASE_URL = "http://localhost:4111";
    public static final String BASIC_AUTHORIZATION_ADMIN = "Basic YWRtaW46YWRtaW4=";

    @BeforeAll
    public static void setupRestAssured(){
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()
        ));
    }

    @AfterAll
    public static void cleanTestData(){
        UserAccount.cleanUsersData();
    }
}
