package ui.steps;

import com.codeborne.selenide.Selenide;
import api.models.LoginUserRequest;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.specs.RequestsSpecs;
import api.specs.ResponseSpecs;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class UserLoginSteps {
    // Логин пользователя
    public static void loginViaApi(String username, String password) {
        String authHeader = new CrudRequester(
                RequestsSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOk()
        ).post(LoginUserRequest.builder()
                        .username(username)
                        .password(password)
                        .build())
                .extract()
                .header("Authorization");

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authHeader);
        Selenide.open("/dashboard");
    }

    // Разлогинить
    public static void logout() {
        executeJavaScript("localStorage.removeItem('authToken');");
        Selenide.refresh();
    }
}
