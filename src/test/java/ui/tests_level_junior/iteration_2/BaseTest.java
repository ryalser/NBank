package ui.tests_level_junior.iteration_2;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import requests.steps.AdminSteps;
import utils.cleanup.UserCleanup;

public class BaseTest {
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

    @AfterAll
    public static void cleanTestData() {
        //UserCleanup.cleanUsers();
        AdminSteps.clearPasswordsCache();
    }
}
