package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class UserDashboard extends BasePage<UserDashboard> {
    private final SelenideElement userDashboard = $(Selectors.byText("User Dashboard"));
    private final SelenideElement welcomeText = $(".welcome-text");
    private final SelenideElement logoutButton = $("button:contains()'Logout'");
    private final SelenideElement depostButton = $("button:contains()'Deposit Money'");
    private final SelenideElement transferButton = $("button:contains()'Make a Transfer'");
    private final SelenideElement createAccountButton = $("button:contains()'Create New Account'");

    // Переопределяем метод по контракту от BasePage - возвращаем url даной страницы
    @Override
    public String url(){
        return "/dashboard";
    }
}