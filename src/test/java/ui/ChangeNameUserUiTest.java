package ui;

import constants.ui.UiTestDataConstants;
import api.generators.RandomData;
import api.models.CreateUserResponse;
import api.models.GetCustomerProfileResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;
import api.requests.steps.ProfileSteps;
import ui.pages.Alerts;
import ui.pages.EditProfilePage;
import ui.pages.UserDashboardPage;
import ui.steps.UserLoginSteps;

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

        AlertHandler.verifyAlert(Alerts.NAME_UPDATED);

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
        String invalidNewName = RandomData.getNameWithoutSpace();

        UserLoginSteps.loginViaApi(username, password);

        //Шаги теста(UI):
        new UserDashboardPage().open().checkTitlePage()
                .checkNameUserInHeader(UiTestDataConstants.DEFAULT_NAME_CAPITALIZED)
                .checkNameUserInTitle(UiTestDataConstants.DEFAULT_NAME_LOWERCASE);


        new EditProfilePage().open().checkTitlePage()
                .changeName(invalidNewName).getSaveChangesButton().click();

        AlertHandler.verifyAlert(Alerts.INVALID_NAME_ERROR);

        // Ожидаемый результат / проверки UI + API:
        new UserDashboardPage().open().checkTitlePage()
                .checkNameUserInHeader(UiTestDataConstants.DEFAULT_NAME_CAPITALIZED)
                .checkNameUserInTitle(UiTestDataConstants.DEFAULT_NAME_LOWERCASE);

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

        UserLoginSteps.loginViaApi(username, password);

        //Шаги теста(UI):
        new UserDashboardPage().open().checkTitlePage()
                .checkNameUserInHeader(UiTestDataConstants.DEFAULT_NAME_CAPITALIZED)
                .checkNameUserInTitle(UiTestDataConstants.DEFAULT_NAME_LOWERCASE);

        new EditProfilePage().open().checkTitlePage().getSaveChangesButton().click();

        AlertHandler.verifyAlert(Alerts.EMPTY_NAME_ERROR);

        // Ожидаемый результат / проверки UI + API:
        new UserDashboardPage().open().checkTitlePage()
                .checkNameUserInHeader(UiTestDataConstants.DEFAULT_NAME_CAPITALIZED)
                .checkNameUserInTitle(UiTestDataConstants.DEFAULT_NAME_LOWERCASE);

        GetCustomerProfileResponse profile = ProfileSteps.getProfile(username, password);
        assertNull(profile.getName());
    }
}