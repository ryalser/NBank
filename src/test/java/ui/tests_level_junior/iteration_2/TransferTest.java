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
import requests.steps.DepositSteps;
import ui.steps.AlertSteps;
import ui.steps.UserLoginSteps;

import java.util.List;
import java.util.Locale;

import static com.codeborne.selenide.Selenide.$;

public class TransferTest extends BaseTest {
    @Test
    @DisplayName("ПОЗИТИВНЫЙ КЕЙС: успешный перевод ДС")
    public void transferWithCorrectData() {
        // Предусловия (API):
        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);

        double depositAmount = RandomData.getDepositAmount();
        double transferAmount = RandomData.getTransferAmount(depositAmount);

        List<UserCreateAccountResponse> accounts = AccountSteps.createTwoAccounts(username, password);
        int senderAccountId = accounts.get(0).getId();
        String senderAccountNumber = accounts.get(0).getAccountNumber();

        int receiverAccountId = accounts.get(1).getId();
        String receiverAccountNumber = accounts.get(1).getAccountNumber();

        DepositSteps.depositToAccount(username, password, senderAccountId, depositAmount);

        // Шаги теста(UI):
        UserLoginSteps.loginViaApi(username, password);

        $(Selectors.byText("\uD83D\uDD04 Make a Transfer"))
                .shouldBe(Condition.visible)
                .click();

        $(".account-selector").shouldBe(Condition.visible)
                .selectOptionContainingText(senderAccountNumber);

        $(Selectors.byAttribute("placeholder", "Enter recipient name"))
                .shouldBe(Condition.visible).setValue(RandomData.getName());

        $(Selectors.byAttribute("placeholder", "Enter recipient account number"))
                .shouldBe(Condition.visible).setValue(receiverAccountNumber);

        $(Selectors.byAttribute("placeholder", "Enter amount"))
                .shouldBe(Condition.visible)
                .setValue(String.valueOf(transferAmount));

        $(Selectors.byId("confirmCheck"))
                .shouldBe(Condition.visible)
                .setSelected(true);

        $(Selectors.byText("\uD83D\uDE80 Send Transfer"))
                .shouldBe(Condition.visible)
                .click();


        // Ожидаемый результат / Асссерты:
        AlertSteps.TransferAlert.verifyTransferSuccess(transferAmount,receiverAccountNumber);

        Selenide.refresh();
        String balanceAccSender = String.format(Locale.US, "ACC%d (Balance: $%.2f)",
                senderAccountId, depositAmount - transferAmount);
        $(".account-selector option[value='" + senderAccountId + "']")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.exactText(balanceAccSender));

        String balanceAccReceiver = String.format(Locale.US, "ACC%d (Balance: $%.2f)",
                receiverAccountId, TestDataConstants.DEFAULT_ACCOUNT_BALANCE + transferAmount);
        $(".account-selector option[value='" + receiverAccountId + "']")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.exactText(balanceAccReceiver));

        softly.assertThat(AccountSteps.getBalanceAccount(username, password, senderAccountId))
                .as("Баланс отправителя после трансфера")
                .isEqualTo(depositAmount - transferAmount);
        softly.assertThat(AccountSteps.getBalanceAccount(username, password, receiverAccountId))
                .as("Баланс получателя после трансфера")
                .isEqualTo(TestDataConstants.DEFAULT_ACCOUNT_BALANCE + transferAmount);
    }

    @Test
    @DisplayName("НЕГАТИВНЫЙ КЕЙС: перевод ДС с невалидной суммой")
    public void transferWitgInvalidData() {
        // Предусловия (API):
        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);

        double depositAmount = RandomData.getDepositAmount();
        double transferAmount = RandomData.getInvalidExceedingAmount();

        List<UserCreateAccountResponse> accounts = AccountSteps.createTwoAccounts(username, password);
        int senderAccountId = accounts.get(0).getId();
        String senderAccountNumber = accounts.get(0).getAccountNumber();

        int receiverAccountId = accounts.get(1).getId();
        String receiverAccountNumber = accounts.get(1).getAccountNumber();

        DepositSteps.depositToAccount(username, password, senderAccountId, depositAmount);

        // Шаги теста(UI):
        UserLoginSteps.loginViaApi(username, password);

        $(Selectors.byText("\uD83D\uDD04 Make a Transfer"))
                .shouldBe(Condition.visible)
                .click();

        $(".account-selector").shouldBe(Condition.visible)
                .selectOptionContainingText(senderAccountNumber);

        $(Selectors.byAttribute("placeholder", "Enter recipient name"))
                .shouldBe(Condition.visible).setValue(RandomData.getName());

        $(Selectors.byAttribute("placeholder", "Enter recipient account number"))
                .shouldBe(Condition.visible).setValue(receiverAccountNumber);

        $(Selectors.byAttribute("placeholder", "Enter amount"))
                .shouldBe(Condition.visible)
                .setValue(String.valueOf(transferAmount));

        $(Selectors.byId("confirmCheck"))
                .shouldBe(Condition.visible)
                .setSelected(true);

        $(Selectors.byText("\uD83D\uDE80 Send Transfer"))
                .shouldBe(Condition.visible)
                .click();

        // Ожидаемый результат / Асссерты:
        AlertSteps.TransferAlert.verifyExceedLimitError();

        Selenide.refresh();
        String balanceAccSender = String.format(Locale.US, "ACC%d (Balance: $%.2f)",
                senderAccountId, depositAmount);
        $(".account-selector option[value='" + senderAccountId + "']")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.exactText(balanceAccSender));

        String balanceAccReceiver = String.format(Locale.US, "ACC%d (Balance: $%.2f)",
                receiverAccountId, TestDataConstants.DEFAULT_ACCOUNT_BALANCE);
        $(".account-selector option[value='" + receiverAccountId + "']")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.exactText(balanceAccReceiver));

        softly.assertThat(AccountSteps.getBalanceAccount(username, password, senderAccountId))
                .as("Баланс отправителя после трансфера")
                .isEqualTo(depositAmount);
        softly.assertThat(AccountSteps.getBalanceAccount(username, password, receiverAccountId))
                .as("Баланс получателя после трансфера")
                .isEqualTo(TestDataConstants.DEFAULT_ACCOUNT_BALANCE);
    }

    @Test
    @DisplayName("НЕГАТИВНЫЙ КЕЙС: перевод ДС без подтверждения (чекбокс не установлен)")
    public void transferWithoutConfirmation(){
        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);

        double depositAmount = RandomData.getDepositAmount();
        double transferAmount = RandomData.getInvalidExceedingAmount();

        List<UserCreateAccountResponse> accounts = AccountSteps.createTwoAccounts(username, password);
        int senderAccountId = accounts.get(0).getId();
        String senderAccountNumber = accounts.get(0).getAccountNumber();

        int receiverAccountId = accounts.get(1).getId();
        String receiverAccountNumber = accounts.get(1).getAccountNumber();

        DepositSteps.depositToAccount(username, password, senderAccountId, depositAmount);

        // Шаги теста(UI):
        UserLoginSteps.loginViaApi(username, password);

        $(Selectors.byText("\uD83D\uDD04 Make a Transfer"))
                .shouldBe(Condition.visible)
                .click();

        $(".account-selector").shouldBe(Condition.visible)
                .selectOptionContainingText(senderAccountNumber);

        $(Selectors.byAttribute("placeholder", "Enter recipient name"))
                .shouldBe(Condition.visible).setValue(RandomData.getName());

        $(Selectors.byAttribute("placeholder", "Enter recipient account number"))
                .shouldBe(Condition.visible).setValue(receiverAccountNumber);

        $(Selectors.byAttribute("placeholder", "Enter amount"))
                .shouldBe(Condition.visible)
                .setValue(String.valueOf(transferAmount));

        $(Selectors.byId("confirmCheck"))
                .shouldBe(Condition.visible)
                .setSelected(false);

        $(Selectors.byText("\uD83D\uDE80 Send Transfer"))
                .shouldBe(Condition.visible)
                .click();

        // Ожидаемый результат / Асссерты:
        AlertSteps.TransferAlert.verifyConfirmationRequiredError();

        Selenide.refresh();
        String balanceAccSender = String.format(Locale.US, "ACC%d (Balance: $%.2f)",
                senderAccountId, depositAmount);
        $(".account-selector option[value='" + senderAccountId + "']")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.exactText(balanceAccSender));

        String balanceAccReceiver = String.format(Locale.US, "ACC%d (Balance: $%.2f)",
                receiverAccountId, TestDataConstants.DEFAULT_ACCOUNT_BALANCE);
        $(".account-selector option[value='" + receiverAccountId + "']")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.exactText(balanceAccReceiver));

        softly.assertThat(AccountSteps.getBalanceAccount(username, password, senderAccountId))
                .as("Баланс отправителя после трансфера")
                .isEqualTo(depositAmount);
        softly.assertThat(AccountSteps.getBalanceAccount(username, password, receiverAccountId))
                .as("Баланс получателя после трансфера")
                .isEqualTo(TestDataConstants.DEFAULT_ACCOUNT_BALANCE);
    }
}