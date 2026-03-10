package ui;

import com.codeborne.selenide.Selenide;
import ui.pages.Alerts;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AlertHandler {
    public static void verifyAlert(Alerts alert) {
        String actual = Selenide.switchTo().alert().getText();
        assertThat(actual).isEqualTo(alert.getMessage());
        Selenide.switchTo().alert().accept();
    }

    public static void verifyAlertContains(Alerts alert, String... parts) {
        String actual = Selenide.switchTo().alert().getText();
        assertThat(actual).contains(alert.getMessage());
        for (String part : parts) {
            assertThat(actual).contains(part);
        }
        Selenide.switchTo().alert().accept();
    }
}
