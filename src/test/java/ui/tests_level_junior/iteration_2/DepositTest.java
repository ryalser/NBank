package ui.tests_level_junior.iteration_2;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import constants.api.TestDataConstants;
import generators.RandomData;
import models.CreateUserResponse;
import models.UserCreateAccountResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.steps.AccountSteps;
import requests.steps.AdminSteps;
import ui.steps.AlertSteps;
import ui.steps.UserLoginSteps;

import java.util.Locale;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;


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
        AlertSteps.DepositAlert.verifyDepositSuccess(amount,accountId);

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
        AlertSteps.DepositAlert.verifyExceedLimitAlert();

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