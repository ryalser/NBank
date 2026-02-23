package ui.ui_tests_level_junior.iteration_1;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import models.CreateUserRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;

public class LoginUserTest {
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
    }

    @Test
    public void adminCanLoginWithCorrectDataTest(){
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
    }
}
