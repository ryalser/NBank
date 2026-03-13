package api.requests.steps;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.GetAllUsers;
import api.models.UserRole;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestsSpecs;
import api.specs.ResponseSpecs;

import java.util.*;

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

    // Получить ID всех пользователей
    public static List<Integer> getIdAllUsers(){
        try {
            GetAllUsers[] usersArray = new CrudRequester(
                    RequestsSpecs.adminSpec(),
                    Endpoint.ADMIN_USER,
                    ResponseSpecs.requestReturnsOk()
            ).get()
                    .extract()
                    .as(GetAllUsers[].class);

            return usersArray == null ? new ArrayList<>() :
                    Arrays.stream(usersArray)
                            .map(GetAllUsers::getId)
                            .toList();

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            return new ArrayList<>();
        }
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