package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {
    private final SelenideElement usernameInput = $(Selectors.byAttribute("placeholder", "username"));
    private final SelenideElement passwordInput = $(Selectors.byAttribute("placeholder","password"));
    private final SelenideElement loginButton = $("button:contains()'Login'");

    // Переопределяем метод по контракту от BasePage - возвращаем url даной страницы
    @Override
    public String url() {
        return "/login";
    }

    // Залогиниться на странице /login
    public LoginPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        loginButton.click();
        return this;
    }
}