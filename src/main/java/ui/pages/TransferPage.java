package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import java.util.Locale;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class TransferPage extends BasePage<TransferPage>{
    private final SelenideElement transferTitle = $(Selectors.byText("\uD83D\uDD04 Make a Transfer"));
    private final SelenideElement listAccounts = $(".account-selector");
    private final SelenideElement recipientNameInput = $(Selectors.byAttribute("placeholder", "Enter recipient name"));
    private final SelenideElement recipientAccountNumberInput = $(Selectors.byAttribute("placeholder", "Enter recipient account number"));
    private final SelenideElement enterAmountInput = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private final SelenideElement confirmationCheckbox = $(Selectors.byId("confirmCheck"));
    private final SelenideElement sendTransferButton = $(Selectors.byText("\uD83D\uDE80 Send Transfer"));

    @Override
    public String url() {
        return "/transfer";
    }

    // Проверить, что страница /transfer открыта
    public TransferPage checkTitlePage() {
        transferTitle.shouldBe(Condition.visible);
        return this;
    }

    // Выбрать нужный аккаунт отправителя
    public TransferPage selectSenderAccountByAccountNumber(String accountNumber) {
        listAccounts.shouldBe(Condition.visible)
                .selectOptionContainingText(accountNumber);
        return this;
    }

    // Проверить баланс аккаунта на фронте
    public TransferPage getBalanceAccountById(int accountId, double amount) {
        String expectedOptionText = String.format(Locale.US, "ACC%d (Balance: $%.2f)",
                accountId, amount);
        $(".account-selector option[value='" + accountId + "']")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.exactText(expectedOptionText));
        return this;
    }

    // Ввести имя получателя
    public TransferPage enterNameRecipient(String recipientName) {
        recipientNameInput.shouldBe(Condition.visible)
                .setValue(recipientName);
        return this;
    }

    // Выбрать аккаунт получателя по Account Number
    public TransferPage enterReceiverAccountNumber(String receiverAccountNumber) {
        recipientAccountNumberInput.shouldBe(Condition.visible)
                .setValue(receiverAccountNumber);
        return this;
    }

    // Ввести сумму перевода
    public TransferPage enterTransferAmount(double amountTransfer) {
        enterAmountInput.shouldBe(Condition.visible)
                .setValue(String.valueOf(amountTransfer));
        return this;
    }

    // Подтвердить операцию
    public TransferPage confirmOperation() {
        confirmationCheckbox.shouldBe(Condition.visible)
                .setSelected(true);
        return this;
    }

    // Оставить операцию неподтвержденной
    public TransferPage doNotConfirmOperation() {
        confirmationCheckbox.shouldBe(Condition.visible)
                .setSelected(false);
        return this;
    }
}