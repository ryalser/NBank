package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import java.util.Locale;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class DepositPage extends BasePage<DepositPage> {
    private final SelenideElement depositTitle = $(Selectors.byText("💰 Deposit Money"));
    private final SelenideElement listAccounts = $(".account-selector");
    private final SelenideElement enterAmountInput = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private final SelenideElement depositButton = $(Selectors.byText("💵 Deposit"));

    @Override
    public String url() {
        return "/deposit";
    }

    // Проверить, что страница /deposit открыта
    public DepositPage checkTitlePage() {
        depositTitle.shouldBe(Condition.visible);
        return this;
    }

    // Выбрать нужный аккаунт
    public DepositPage selectAccountByAccountNumber(String accountNumber) {
        listAccounts.shouldBe(Condition.visible)
                .selectOptionContainingText(accountNumber);
        return this;
    }

    // Ввести сумму пополнения
    public DepositPage enterAmountDeposit(double amount) {
        enterAmountInput.shouldBe(Condition.visible).setValue(String.valueOf(amount));
        return this;
    }

    // Проверить баланс аккаунта на фронте
    public DepositPage getBalanceAcciuntById(int accountId, double amount) {
        String expectedOptionText = String.format(Locale.US, "ACC%d (Balance: $%.2f)",
                accountId, amount);
        $(".account-selector option[value='" + accountId + "']")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.exactText(expectedOptionText));
        return this;
    }
}