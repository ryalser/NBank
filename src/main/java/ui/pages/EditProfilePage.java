package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class EditProfilePage extends BasePage<EditProfilePage> {
    private final SelenideElement username = $(".user-name");
    private final SelenideElement editProfileTitle = $(Selectors.byText("✏️ Edit Profile"));
    private final SelenideElement enterNewNameInput = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private final SelenideElement saveChangesButton = $(Selectors.byText("💾 Save Changes"));
    private final SelenideElement logoutButton = $(Selectors.byText("Logout"));


    @Override
    public String url() {
        return "/edit-profile";
    }

    // Проверить, что страница /edit-profile открыта
    public EditProfilePage checkTitlePage() {
        editProfileTitle.shouldBe(Condition.visible);
        return this;
    }

    // Ввести новое имя профиля
    public EditProfilePage changeName(String newName) {
        enterNewNameInput.setValue(newName);
        return this;
    }

    // Проверить имя пользователя
    public EditProfilePage checkNameUser(String newName) {
        username.shouldHave(Condition.text(newName));
        return this;
    }
}