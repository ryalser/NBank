package api_tests_level_junior.iteration2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;

import static io.restassured.RestAssured.given;

public class BaseTest {
    protected static final String BASE_URL = "http://localhost:4111";
    protected static final String BASIC_AUTHORIZATION_ADMIN = "Basic YWRtaW46YWRtaW4=";

    @BeforeAll
    public static void setupRestAssured(){
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()
        ));
    }
}
