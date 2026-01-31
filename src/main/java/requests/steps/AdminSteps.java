package requests.steps;

import generators.RandomModelGenerator;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.UserRole;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestsSpecs;
import specs.ResponseSpecs;

import java.util.HashMap;
import java.util.Map;

public class AdminSteps {
    // Хранение НЕ хешированных паролей после создания юзеров
    private static final Map<String, String> arrayPasswords = new HashMap<>();

    // Создать пользователя, роль - ADMIN
    public static CreateUserResponse createUserAsAdmin(){
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        userRequest.setRole(UserRole.ADMIN.toString());

        String username = userRequest.getUsername();
        String originalPassword = userRequest.getPassword();
        arrayPasswords.put(username,originalPassword);

        return new ValidatedCrudRequester<CreateUserResponse>(
                RequestsSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated()
        ).post(userRequest);
    }

    // Создать пользователя, роль - USER
    public static CreateUserResponse createUserAsUser(){
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        userRequest.setRole(UserRole.USER.toString());

        String username = userRequest.getUsername();
        String originalPassword = userRequest.getPassword();
        arrayPasswords.put(username,originalPassword);

        return new ValidatedCrudRequester<CreateUserResponse>(
                RequestsSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated()
        ).post(userRequest);
    }

    public static CreateUserRequest generateUserRequest(){
        return RandomModelGenerator.generate(CreateUserRequest.class);
    }

    // Получение оригинального пароля, чтобы вернуть не хэшированный
    public static String getOriginalPassword(String username) {
        return arrayPasswords.get(username);
    }

    // Очистка паролей
    public static void clearPasswordsCache() {
        arrayPasswords.clear();
    }
}