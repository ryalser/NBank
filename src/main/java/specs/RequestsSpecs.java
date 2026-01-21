package specs;

import configs.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import models.LoginUserRequest;
import requests.LoginUserRequester;

import java.util.List;

public class RequestsSpecs {
    private RequestsSpecs() {}

    // Базовый билдер для всех спецификаций; будет использован в спеках админа и юзера
    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(
                        new RequestLoggingFilter(),
                        new ResponseLoggingFilter()
                ))
                .setBaseUri(Config.getProperty("server") + Config.getProperty("apiVersion"));
    }

    // Спецификация без аутентификации
    public static RequestSpecification unAuthSpec() {
        return defaultRequestBuilder().build();
    }

    // Спецификация для админа
    public static RequestSpecification adminSpec() {
        return defaultRequestBuilder()
                .addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
                .build();
    }

    // Спецификация для аутентифицированного пользователя
    public static RequestSpecification authAsUser(String username, String password) {
        String userAuthHeader = new LoginUserRequester(
                RequestsSpecs.unAuthSpec(),
                ResponseSpecs.requestReturnsOk())
                .post(LoginUserRequest.builder().username(username).password(password).build())
                .extract()
                .header("Authorization");

        return defaultRequestBuilder()
                .addHeader("Authorization", userAuthHeader)
                .build();
    }
}
