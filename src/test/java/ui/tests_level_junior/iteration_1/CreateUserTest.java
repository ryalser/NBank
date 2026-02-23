package ui.tests_level_junior.iteration_1;

import com.codeborne.selenide.*;
import generators.RandomModelGenerator;
import models.CreateUserRequest;
import models.CreateUserResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import specs.RequestsSpecs;

import java.util.Arrays;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateUserTest {
    @BeforeAll
    public static void setupSelenoid(){
       /*
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.1.110:3000";
        Configuration.browser = "chrome";
        Configuration.browserVersion = "91.0";
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 10000;

        // Создаем ChromeOptions и настраиваем selenoid options
        ChromeOptions options = new ChromeOptions();

        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("enableLog", true);

        options.setCapability("selenoid:options", selenoidOptions);

        Configuration.browserCapabilities = options;
        */
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.headless = false; // false - показывать браузер
        Configuration.baseUrl = "http://localhost:3000";
        Configuration.timeout = 6000;
    }

    @Test
    public void adminCanCreateUserTest() {
        CreateUserRequest admin = CreateUserRequest.builder()
                .username("admin")
                .password("admin")
                .build();

        Selenide.open("/login");
        $(Selectors.byAttribute("placeholder","Username"))
                .sendKeys(admin.getUsername());
        $(Selectors.byAttribute("placeholder","Password"))
                .sendKeys(admin.getPassword());
        $("button").click();

        $(Selectors.byText("Admin Panel"))
                .shouldBe(Condition.visible);

        CreateUserRequest newUser = RandomModelGenerator.generate(CreateUserRequest.class);

        $(Selectors.byAttribute("placeholder","Username"))
                .sendKeys(admin.getUsername());
        $(Selectors.byAttribute("placeholder","Password"))
                .sendKeys(admin.getPassword());
        $(Selectors.byText("Add User"));

        Alert alert = switchTo().alert();

        assertEquals("✅ User created successfully!", alert.getText());
        alert.accept();


        ElementsCollection allUSersFromDashboard = $(Selectors.byText("All Users"))
                .parent().findAll("li");
        allUSersFromDashboard.findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldBe(Condition.visible);

        CreateUserResponse[] users = given()
                .spec(RequestsSpecs.adminSpec())
                .get("http://localhost:4111/api/v1/admin/users")
                .then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(CreateUserResponse[].class);

        // незавершенный
    }
}
