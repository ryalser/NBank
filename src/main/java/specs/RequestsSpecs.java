package specs;

import configs.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import models.LoginUserRequest;
import requests.LoginUserRequester;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestsSpecs {
    // Кэш авторизационных заголовков для пользователей
    private static Map<String,String> authHeaders = new HashMap<>(Map.of(
            "admin", "Basic YWRtaW46YWRtaW4="
    ));
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
                .addHeader("Authorization", authHeaders.get("admin"))
                .build();
    }

    // Спецификация для аутентифицированного пользователя
    public static RequestSpecification authAsUser(String username, String password) {
        if (!authHeaders.containsKey(username)){
            String userAuthHeader = getAuthHeaderFromLogin(username, password);
            authHeaders.put(username, userAuthHeader);
        }

        return defaultRequestBuilder()
                .addHeader("Authorization", authHeaders.get(username))
                .build();
    }

    private static String getAuthHeaderFromLogin(String username, String password){
        LoginUserRequest loginRequest = LoginUserRequest.builder()
                .username(username)
                .password(password)
                .build();

        return new CrudRequester(unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOk())
                .post(loginRequest)
                .extract()
                .header("Authorization");
    }

    // Очистка кэша токенов
    public static void clearAuthCache(){
        authHeaders.clear();
        authHeaders.put("admin","Basic YWRtaW46YWRtaW4=");
    }

    // Метод для получения спецификации с произвольным заголовком
    public static RequestSpecification withHeader(String headerName, String headerValue){
        return defaultRequestBuilder()
                .addHeader(headerName,headerValue)
                .build();
    }
}