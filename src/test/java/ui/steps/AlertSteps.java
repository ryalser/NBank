package ui.steps;

import constants.ui.UiMessages;
import org.openqa.selenium.Alert;

import java.util.Locale;

import static com.codeborne.selenide.Selenide.switchTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlertSteps {
    private AlertSteps() {
    }

    public static class ProfileAlert {
        private ProfileAlert() {
        }

        // Алерт: успешное обновление имени
        public static void verifyNameUpdated() {
            Alert alert = switchTo().alert();
            assertEquals(UiMessages.Success.NAME_UPDATED, alert.getText());
            alert.accept();
        }

        // Алерт: ошибка при некорректном имени пользователя
        public static void verifyIncorrrectNameError() {
            Alert alert = switchTo().alert();
            assertEquals(UiMessages.Error.INVALID_NAME, alert.getText());
            alert.accept();
        }

        // Алерт: ошибка при пустом имени
        public static void verifyEmptyNameError() {
            Alert alert = switchTo().alert();
            assertEquals(UiMessages.Error.EMPTY_NAME, alert.getText());
            alert.accept();
        }
    }

    public static class DepositAlert {
        private DepositAlert() {
        }

        // Алерт: проверка успешного пополнения
        public static void verifyDepositSuccess(double amount, int accountId) {
            Alert alert = switchTo().alert();
            String actualMessage = alert.getText();

            assertTrue(actualMessage.startsWith("✅ Successfully deposited $"));
            assertTrue(actualMessage.contains("to account ACC" + accountId));
            assertTrue(actualMessage.contains(String.format(Locale.US, "%.2f", amount)));

            alert.accept();
        }

        // Алерт: проверка проверка превышения лимита
        public static void verifyExceedLimitAlert() {
            Alert alert = switchTo().alert();
            String actualMessage = alert.getText();
            assertTrue(actualMessage.contains(UiMessages.Error.DEPOSIT_EXCEED_LIMIT));

            alert.accept();
        }
    }

    public static class TransferAlert {
        private TransferAlert() {
        }

        // Алерт: проверка успешного перевода
        public static void verifyTransferSuccess(double amount, String receiverAccountNumber) {
            Alert alert = switchTo().alert();
            String actualMessage = alert.getText();

            assertTrue(actualMessage.startsWith("✅ Successfully transferred $"));
            assertTrue(actualMessage.contains("to account " + receiverAccountNumber));
            assertTrue(actualMessage.contains(String.format(Locale.US, "%.2f", amount)));

            alert.accept();
        }

        // Алерт: проверка ошибки превышения лимита
        public static void verifyExceedLimitError() {
            Alert alert = switchTo().alert();
            String actualMessage = alert.getText();
            assertTrue(actualMessage.contains(UiMessages.Error.ERROR_TRANSFER_AMOUNT_EXCEEDS_LIMIT));
            alert.accept();
        }

        // Алерт: проверка ошибки без подтверждения (чекбокс)
        public static void verifyConfirmationRequiredError() {
            Alert alert = switchTo().alert();
            String actualMessage = alert.getText();
            assertTrue(actualMessage.contains(UiMessages.Error.CONFIRMATION_REQUIRED_ERROR));
            alert.accept();
        }
    }
}