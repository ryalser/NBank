package ui;

import api.configs.Config;
import com.codeborne.selenide.Configuration;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.chrome.ChromeOptions;
import api.requests.steps.AdminSteps;
import api.specs.RequestsSpecs;
import ui.steps.UserLoginSteps;
import api.utils.cleanup.UserCleanup;

import java.util.HashMap;
import java.util.Map;

public class BaseUiTest {
    protected SoftAssertions softly;

    @BeforeAll
    public static void setupSelenoid() {

//        Configuration.remote = Config.getProperty("uiRemote");
//        Configuration.baseUrl = Config.getProperty("uiBaseUrl");
//        Configuration.browser = Config.getProperty("browser");
//        Configuration.browserVersion = Config.getProperty("browserVersion");
//        Configuration.browserSize = Config.getProperty("browserSize");;
//        Configuration.timeout = 10000;

        // Создаем ChromeOptions и настраиваем selenoid options
//        ChromeOptions options = new ChromeOptions();
//
//        Map<String, Object> selenoidOptions = new HashMap<>();
//        selenoidOptions.put("enableVNC", true);
//        selenoidOptions.put("enableLog", true);
//
//        options.setCapability("selenoid:options", selenoidOptions);
//
//        Configuration.browserCapabilities = options;

        //Локальный запуск с selenide
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.headless = false; // false - показывать браузер
        Configuration.baseUrl = "http://localhost:3000";
        Configuration.timeout = 6000;

    }

    @BeforeEach
    public void setupTest() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void afterTest() {
        softly.assertAll();
        RequestsSpecs.clearAuthCache();
        UserLoginSteps.logout();
    }

    @AfterAll
    public static void cleanTestData() {
        //UserCleanup.cleanUsers();
        AdminSteps.clearPasswordsCache();
    }
}