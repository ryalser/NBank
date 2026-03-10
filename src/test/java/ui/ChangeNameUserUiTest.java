package ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import constants.ui.UiMessages;
import constants.ui.UiTestDataConstants;
import api.generators.RandomData;
import api.models.CreateUserResponse;
import api.models.GetCustomerProfileResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;
import api.requests.steps.ProfileSteps;
import ui.pages.EditProfilePage;
import ui.pages.UserDashboardPage;
import ui.steps.AlertSteps;
import ui.steps.UserLoginSteps;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ChangeNameUserUiTest extends BaseUiTest {
    @Test
    @DisplayName("ПОЗИТИВНЫЙ КЕЙС: успешное изменение имени пользователя")
    public void changeNameTest() {
        // Предусловия (API):
        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);
        String newName = RandomData.getName();

        UserLoginSteps.loginViaApi(username, password);

        // Шаги теста(UI):
        new UserDashboardPage().open().checkTitlePage()
                .checkNameUserInHeader(UiTestDataConstants.DEFAULT_NAME_CAPITALIZED)
                .checkNameUserInTitle(UiTestDataConstants.DEFAULT_NAME_LOWERCASE);


        new EditProfilePage().open().checkTitlePage()
                        .changeName(newName).getSaveChangesButton().click();

        AlertSteps.ProfileAlert.verifyNameUpdated();

        // Ожидаемый результат / проверки UI + API:
        new UserDashboardPage().open()
                .checkNameUserInHeader(newName)
                .checkNameUserInTitle(newName);

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

        AlertSteps.ProfileAlert.verifyIncorrrectNameError();

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

        AlertSteps.ProfileAlert.verifyEmptyNameError();

        Selenide.open("/dashboard");

        // Ожидаемый результат / проверки UI + API:
        $(".user-name").shouldHave(Condition.text(UiTestDataConstants.DEFAULT_NAME_CAPITALIZED));
        $(".welcome-text").shouldBe(Condition.visible)
                .shouldHave(Condition.text("Welcome, " + UiTestDataConstants.DEFAULT_NAME_LOWERCASE + "!"));

        GetCustomerProfileResponse profile = ProfileSteps.getProfile(username, password);
        assertNull(profile.getName());
    }
}