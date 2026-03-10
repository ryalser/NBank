package ui;

import constants.api.TestDataConstants;
import api.generators.RandomData;
import api.models.CreateUserResponse;
import api.models.UserCreateAccountResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import api.requests.steps.AccountSteps;
import api.requests.steps.AdminSteps;
import ui.pages.Alerts;
import ui.pages.DepositPage;
import ui.steps.UserLoginSteps;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DepositUiTest extends BaseUiTest {
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

        UserLoginSteps.loginViaApi(username, password);

        //Шаги теста(UI):
        new DepositPage().open().checkTitlePage().selectAccountByAccountNumber(accountNumber)
                .enterAmountDeposit(amount)
                .getDepositButton().click();

        AlertHandler.verifyAlertContains(Alerts.DEPOSIT_SUCCESS,
                String.valueOf(amount),
                String.valueOf(accountId));

        //Ожидаемый результат / Асссерты:
        new DepositPage().open().getBalanceAcciuntById(accountId,amount);

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

        UserLoginSteps.loginViaApi(username, password);

        //Шаги теста(UI):
        new DepositPage().open().checkTitlePage().selectAccountByAccountNumber(accountNumber)
                .enterAmountDeposit(RandomData.getInvalidExceedingAmount())
                .getDepositButton().click();

        //Ожидаемый результат / Асссерты:
        AlertHandler.verifyAlert(Alerts.DEPOSIT_EXCEED_LIMIT);

        new DepositPage().open().getBalanceAcciuntById(accountId,TestDataConstants.DEFAULT_ACCOUNT_BALANCE);

        assertEquals(TestDataConstants.DEFAULT_ACCOUNT_BALANCE,
                AccountSteps.getBalanceAccount(username, password, accountId));
    }
}