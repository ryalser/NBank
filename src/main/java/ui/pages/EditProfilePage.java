package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class EditProfilePage extends BasePage<EditProfilePage> {
    private final SelenideElement username = $(".user-name");
    private final SelenideElement editProfileTitle = $(".container h1:contains('Edit Profile')");
    private final SelenideElement enterNewNameInput = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private final SelenideElement saveChangesButton = $("button:contains('Save Changes')");
    private final SelenideElement logoutButton = $("button:contains()'Logout'");


    @Override
    public String url() {
        return "/edit-profile";
    }

    // Проверить, что страница /edit-profile открыта
    public EditProfilePage checkTitlePage() {
        editProfileTitle.shouldBe(Condition.visible);
        return this;
    }

    // Изменить имя профиля и нажать "сохранить"
    public EditProfilePage changeName(String newName) {
        enterNewNameInput.setValue(newName);
        saveChangesButton.click();
        return this;
    }

    // Проверить имя пользователя
    public EditProfilePage checkNameUser(String newName) {
        username.shouldHave(Condition.text(newName));
        return this;
    }
}