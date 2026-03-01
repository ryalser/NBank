package ui.tests_level_junior.iteration_2;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import constants.ui.UiMessages;
import constants.ui.UiTestDataConstants;
import generators.RandomData;
import models.CreateUserResponse;
import models.GetCustomerProfileResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import requests.steps.AdminSteps;
import requests.steps.ProfileSteps;
import ui.steps.UserLoginSteps;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ChangeNameUserTest extends BaseTest {
    @Test
    @DisplayName("ПОЗИТИВНЫЙ КЕЙС: успешное изменение имени пользователя")
    public void changeNameTest() {
        // Предусловия (API):
        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);
        String newName = RandomData.getName();

        // Шаги теста(UI):
        UserLoginSteps.loginViaApi(username, password);

        $(Selectors.byText("User Dashboard")).shouldBe(Condition.visible);

        $(".welcome-text").shouldBe(Condition.visible)
                .shouldHave(Condition.text(UiMessages.Welcome.DEFAULT_GREETING));

        $(".user-username").click();

        $(".container.mt-5.text-center h1").shouldBe(Condition.visible)
                .shouldHave(Condition.exactText("✏️ Edit Profile"));

        $(Selectors.byAttribute("placeholder", "Enter new name"))
                .setValue(newName)
                .shouldHave(Condition.value(newName));

        $(Selectors.byText("💾 Save Changes"))
                .shouldBe(Condition.visible).click();

        Alert alert = switchTo().alert();
        assertEquals(UiMessages.Success.NAME_UPDATED, alert.getText());
        alert.accept(); // закрываем алерт

        Selenide.open("/dashboard");

        // Ожидаемый результат / проверки UI + API:
        $(".user-name").shouldHave(Condition.text(newName));
        $(".welcome-text").shouldBe(Condition.visible)
                .shouldHave(Condition.text("Welcome, " + newName + "!"));

        GetCustomerProfileResponse profile = ProfileSteps.getProfile(username, password);
        assertEquals(newName, profile.getName());
    }

    @Test
    @DisplayName("НЕГАТИВНЫЙ КЕЙС: некорректное имя пользователя при редактировании профиля")
    public void changeNameWithInvalidDataTest() {
        // Предусловия (API):
        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);
        String newName = RandomData.getNameWithoutSpace();

        //Шаги теста(UI):
        UserLoginSteps.loginViaApi(username, password);

        $(Selectors.byText("User Dashboard")).shouldBe(Condition.visible);
        $(".welcome-text").shouldBe(Condition.visible)
                .shouldHave(Condition.text(UiMessages.Welcome.DEFAULT_GREETING));
        $(Selectors.byClassName("user-username")).click();

        $(".container.mt-5.text-center h1").shouldBe(Condition.visible)
                .shouldHave(Condition.exactText("✏️ Edit Profile"));

        $(Selectors.byAttribute("placeholder", "Enter new name"))
                .setValue(newName)
                .shouldHave(Condition.value(newName));

        $(Selectors.byText("💾 Save Changes"))
                .shouldBe(Condition.visible).click();

        // Проверка алерта
        Alert alert = switchTo().alert();
        assertEquals(UiMessages.Error.INVALID_NAME, alert.getText());
        alert.accept();

        Selenide.open("/dashboard");

        // Ожидаемый результат / проверки UI + API:
        $(".user-name").shouldHave(Condition.text(UiTestDataConstants.DEFAULT_NAME_CAPITALIZED));
        $(".welcome-text").shouldBe(Condition.visible)
                .shouldHave(Condition.text("Welcome, " + UiTestDataConstants.DEFAULT_NAME_LOWERCASE + "!"));

        GetCustomerProfileResponse profile = ProfileSteps.getProfile(username, password);
        assertNull(profile.getName());
    }

    @Test
    @DisplayName("НЕГАТИВНЫЙ КЕЙС: пустое имя пользователя")
    public void changeNameWithEmptyValueTest() {
        // Предусловия (API):
        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);

        //Шаги теста(UI):
        Selenide.open("/login");
        $(Selectors.byAttribute("placeholder", "Username"))
                .shouldBe(Condition.visible).sendKeys(username);
        $(Selectors.byAttribute("placeholder", "Password"))
                .shouldBe(Condition.visible).sendKeys(password);
        $("button").click();

        $(Selectors.byText("User Dashboard")).shouldBe(Condition.visible);
        $(".welcome-text").shouldBe(Condition.visible)
                .shouldHave(Condition.text(UiMessages.Welcome.DEFAULT_GREETING));
        $(Selectors.byClassName("user-username")).click();

        $(".container.mt-5.text-center h1").shouldBe(Condition.visible)
                .shouldHave(Condition.exactText("✏️ Edit Profile"));

        $(Selectors.byText("💾 Save Changes"))
                .shouldBe(Condition.visible).click();

        // Проверка алерта
        Alert alert = switchTo().alert();
        assertEquals(UiMessages.Error.EMPTY_NAME, alert.getText());
        alert.accept();

        Selenide.open("/dashboard");

        // Ожидаемый результат / проверки UI + API:
        $(".user-name").shouldHave(Condition.text(UiTestDataConstants.DEFAULT_NAME_CAPITALIZED));
        $(".welcome-text").shouldBe(Condition.visible)
                .shouldHave(Condition.text("Welcome, " + UiTestDataConstants.DEFAULT_NAME_LOWERCASE + "!"));

        GetCustomerProfileResponse profile = ProfileSteps.getProfile(username, password);
        assertNull(profile.getName());
    }
}