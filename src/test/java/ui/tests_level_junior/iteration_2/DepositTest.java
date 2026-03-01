package ui.tests_level_junior.iteration_2;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import constants.api.TestDataConstants;
import constants.ui.UiMessages;
import generators.RandomData;
import models.CreateUserResponse;
import models.UserCreateAccountResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import requests.steps.AccountSteps;
import requests.steps.AdminSteps;
import ui.steps.UserLoginSteps;

import java.util.Locale;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DepositTest extends BaseTest {
    @Test
    @DisplayName("ПОЗИТИВНЫЙ КЕЙС: успешное пополнение")
    public void depisitWithCorrectData() {
        // Предусловия (API):
        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);
        double amount = RandomData.getDepositAmount();

        UserCreateAccountResponse account = AccountSteps.createAccount(username, password);
        int accountId = account.getId();
        String accountNumber = account.getAccountNumber();

        //Шаги теста(UI):
        UserLoginSteps.loginViaApi(username, password);

        $(Selectors.byText("User Dashboard")).shouldBe(Condition.visible);

        $(Selectors.byText("💰 Deposit Money"))
                .shouldBe(Condition.visible)
                .click();

        $(".account-selector").shouldBe(Condition.visible)
                .selectOptionContainingText(accountNumber);


        $(Selectors.byAttribute("placeholder", "Enter amount"))
                .shouldBe(Condition.visible).setValue(String.valueOf(amount));

        $(Selectors.byText("💵 Deposit"))
                .shouldBe(Condition.visible)
                .shouldBe(Condition.enabled)
                .click();

        //Ожидаемый результат / Асссерты:
        Alert alert = switchTo().alert();
        String actualMessage = alert.getText();
        assertTrue(actualMessage.startsWith("✅ Successfully deposited $"));
        assertTrue(actualMessage.contains("to account ACC" + accountId));
        assertTrue(actualMessage.contains(String.format(Locale.US, "%.2f", amount)));
        alert.accept();

        Selenide.open("/deposit");

        String expectedOptionText = String.format(Locale.US, "ACC%d (Balance: $%.2f)",
                accountId, amount);
        $(".account-selector option[value='" + accountId + "']")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.exactText(expectedOptionText));

        assertEquals(TestDataConstants.DEFAULT_ACCOUNT_BALANCE + amount,
                AccountSteps.getBalanceAccount(username, password, accountId));
    }

    @Test
    @DisplayName("НЕГАТИВНЫЙ КЕЙС: пополнение аккаунта с невалидными данными")
    public void depositWithInvalidData() {
        // Предусловия (API):
        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);

        UserCreateAccountResponse account = AccountSteps.createAccount(username, password);
        int accountId = account.getId();
        String accountNumber = account.getAccountNumber();

        //Шаги теста(UI):
        UserLoginSteps.loginViaApi(username, password);

        $(Selectors.byText("User Dashboard")).shouldBe(Condition.visible);

        $(Selectors.byText("💰 Deposit Money"))
                .shouldBe(Condition.visible)
                .click();

        $(".account-selector").shouldBe(Condition.visible)
                .selectOptionContainingText(accountNumber);


        $(".account-selector").shouldBe(Condition.visible)
                .selectOptionContainingText(accountNumber);

        $(Selectors.byAttribute("placeholder", "Enter amount"))
                .shouldBe(Condition.visible).setValue(String.valueOf(RandomData.getInvalidExceedingAmount()));

        $(Selectors.byText("💵 Deposit")).click();

        //Ожидаемый результат / Асссерты:
        Alert alert = switchTo().alert();
        String actualMessage = alert.getText();
        assertTrue(actualMessage.contains(UiMessages.Error.DEPOSIT_EXCEED_LIMIT));
        alert.accept();

        Selenide.open("/deposit");

        String expectedOptionText = String.format(Locale.US, "ACC%d (Balance: $%.2f)",
                accountId, TestDataConstants.DEFAULT_ACCOUNT_BALANCE);
        $(".account-selector option[value='" + accountId + "']")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.exactText(expectedOptionText));

        assertEquals(TestDataConstants.DEFAULT_ACCOUNT_BALANCE,
                AccountSteps.getBalanceAccount(username, password, accountId));
    }
}
