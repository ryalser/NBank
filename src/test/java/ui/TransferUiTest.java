package ui;

import com.codeborne.selenide.Selenide;
import constants.api.TestDataConstants;
import api.generators.RandomData;
import api.models.CreateUserResponse;
import api.models.UserCreateAccountResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import api.requests.steps.AccountSteps;
import api.requests.steps.AdminSteps;
import api.requests.steps.DepositSteps;
import ui.pages.Alerts;
import ui.pages.TransferPage;

import java.util.List;

public class TransferUiTest extends BaseUiTest {
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

        UserLogin.loginViaApi(username, password);

        // Шаги теста(UI):
        new TransferPage().open().checkTitlePage()
                .selectSenderAccountByAccountNumber(senderAccountNumber)
                .enterNameRecipient(RandomData.getName())
                .enterReceiverAccountNumber(receiverAccountNumber)
                .enterTransferAmount(transferAmount)
                .confirmOperation()
                .getSendTransferButton().click();

        // Ожидаемый результат / Асссерты:
        AlertHandler.verifyAlertContains(Alerts.TRANSFER_SUCCESS,
                String.valueOf(transferAmount),
                String.valueOf(receiverAccountNumber));

        Selenide.refresh();
        new TransferPage()
                .getBalanceAccountById(senderAccountId,depositAmount - transferAmount)
                .getBalanceAccountById(receiverAccountId,TestDataConstants.DEFAULT_ACCOUNT_BALANCE + transferAmount);

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

        UserLogin.loginViaApi(username, password);

        // Шаги теста(UI):
        new TransferPage().open().checkTitlePage()
                .selectSenderAccountByAccountNumber(senderAccountNumber)
                .enterNameRecipient(RandomData.getName())
                .enterReceiverAccountNumber(receiverAccountNumber)
                .enterTransferAmount(transferAmount)
                .confirmOperation()
                .getSendTransferButton().click();


        // Ожидаемый результат / Асссерты:
        AlertHandler.verifyAlert(Alerts.TRANSFER_EXCEED_LIMIT);

        Selenide.refresh();
        new TransferPage()
                .getBalanceAccountById(senderAccountId,depositAmount)
                .getBalanceAccountById(receiverAccountId,TestDataConstants.DEFAULT_ACCOUNT_BALANCE);

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

        UserLogin.loginViaApi(username, password);

        // Шаги теста(UI):
        new TransferPage().open().checkTitlePage()
                .selectSenderAccountByAccountNumber(senderAccountNumber)
                .enterNameRecipient(RandomData.getName())
                .enterReceiverAccountNumber(receiverAccountNumber)
                .enterTransferAmount(transferAmount)
                .doNotConfirmOperation()
                .getSendTransferButton().click();

        // Ожидаемый результат / Асссерты:
        AlertHandler.verifyAlert(Alerts.CONFIRMATION_REQUIRED);

        Selenide.refresh();
        new TransferPage()
                .getBalanceAccountById(senderAccountId,depositAmount)
                .getBalanceAccountById(receiverAccountId,TestDataConstants.DEFAULT_ACCOUNT_BALANCE);

        softly.assertThat(AccountSteps.getBalanceAccount(username, password, senderAccountId))
                .as("Баланс отправителя после трансфера")
                .isEqualTo(depositAmount);
        softly.assertThat(AccountSteps.getBalanceAccount(username, password, receiverAccountId))
                .as("Баланс получателя после трансфера")
                .isEqualTo(TestDataConstants.DEFAULT_ACCOUNT_BALANCE);
    }
}