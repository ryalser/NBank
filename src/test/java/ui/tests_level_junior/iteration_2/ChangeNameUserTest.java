package ui.tests_level_junior.iteration_2;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import generators.RandomData;
import models.CreateUserResponse;
import models.GetCustomerProfileResponse;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import requests.steps.AdminSteps;
import requests.steps.ProfileSteps;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChangeNameUserTest extends BaseTest {
    @Test
    public void changeNameTest(){
       // Предусловия (подготовка через API):

        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);
        String newName = RandomData.getName();


        Selenide.open("/login");
        $(Selectors.byAttribute("placeholder","Username"))
                .shouldBe(Condition.visible).sendKeys(username);
        $(Selectors.byAttribute("placeholder","Password"))
                .shouldBe(Condition.visible).sendKeys(password);
        $("button").click();

        $(Selectors.byText("User Dashboard")).shouldBe(Condition.visible);


       // Шаги теста(UI):
        $(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible)
                .shouldHave(Condition.text("Welcome, noname!"));

        $(Selectors.byClassName("user-username")).click();

        $(".container.mt-5.text-center h1").shouldBe(Condition.visible)
                .shouldHave(Condition.exactText("✏️ Edit Profile"));

        $(Selectors.byAttribute("placeholder","Enter new name"))
                .sendKeys(newName);

        $(Selectors.byText("💾 Save Changes"))
                .shouldBe(Condition.visible).click();

        Alert alert = switchTo().alert();
        assertEquals("✅ Name updated successfully!", alert.getText());

        Selenide.open("/dashboard");

        // Ожидаемый результат / проверки UI + API:
        $(".user-name").shouldHave(Condition.text(newName));
        $(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible)
                .shouldHave(Condition.text("Welcome, " + newName + "!"));

        GetCustomerProfileResponse profile = ProfileSteps.getProfile(username, password);
        assertEquals(newName, profile.getName());
    }
}