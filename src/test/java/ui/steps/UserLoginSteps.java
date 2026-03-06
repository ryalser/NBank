package ui.steps;

import com.codeborne.selenide.Selenide;
import models.LoginUserRequest;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import specs.RequestsSpecs;
import specs.ResponseSpecs;

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
