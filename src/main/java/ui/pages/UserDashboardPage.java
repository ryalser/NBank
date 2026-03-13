package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class UserDashboardPage extends BasePage<UserDashboardPage> {
    private final SelenideElement userDashboard = $(Selectors.byText("User Dashboard"));
    private final SelenideElement username = $(".user-name");
    private final SelenideElement welcomeText = $(".welcome-text");
    private final SelenideElement logoutButton = $("button:contains('Logout')");
    private final SelenideElement depositButton = $("button:contains('Deposit Money')");
    private final SelenideElement transferButton = $("button:contains('Make a Transfer')");
    private final SelenideElement createAccountButton = $("button:contains('Create New Account')");

    // Переопределяем метод по контракту от BasePage - возвращаем url даной страницы
    @Override
    public String url(){
        return "/dashboard";
    }

    // Проверить, что мы на странице User Dashboard
    public UserDashboardPage checkTitlePage() {
        userDashboard.shouldBe(Condition.visible);
        return this;
    }

    // Проверить имя пользователя в хедери страницы
    public UserDashboardPage checkNameUserInHeader(String newName) {
        username.shouldHave(Condition.text(newName));
        return this;
    }

    // Проверить имя пользователя в заголовке странице(приветствие)
    public UserDashboardPage checkNameUserInTitle(String newName) {
        welcomeText.shouldBe(Condition.visible)
                .shouldHave(Condition.text(
                        "Welcome, " + newName + "!"));
        return this;
    }
}