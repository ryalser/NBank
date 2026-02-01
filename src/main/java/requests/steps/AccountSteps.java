package requests.steps;

import io.restassured.response.ValidatableResponse;
import models.Accounts;
import models.UserCreateAccountResponse;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestsSpecs;
import specs.ResponseSpecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AccountSteps {
    // Создание аккаунта
    public static UserCreateAccountResponse createAccount(String username, String password){

        return new ValidatedCrudRequester<UserCreateAccountResponse>(
                RequestsSpecs.authAsUser(username, password),
                Endpoint.CREATE_ACCOUNT,
                ResponseSpecs.entityWasCreated()
        ).post();
    }

    // Создание двух аккаунтов пользователем
    public static List<UserCreateAccountResponse> createTwoAccounts(String username, String password){
        List<UserCreateAccountResponse> createdAccounts = new ArrayList<>();

        // Создаем первый аккаунт
        UserCreateAccountResponse firstAccount = createAccount(username, password);
        createdAccounts.add(firstAccount);

        // Создаем второй аккаунт
        UserCreateAccountResponse secondAccount = createAccount(username, password);
        createdAccounts.add(secondAccount);

        return createdAccounts;
    }

    // Получение аккаунтов пользователя
    public static List<Accounts> getUSerAccounts(String username, String password){
        ValidatableResponse response = new CrudRequester(
                RequestsSpecs.authAsUser(username, password),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOk()
        ).get();

        // извлечь массив аккаунтов в List
        Accounts[] accountsArray = response.extract().as(Accounts[].class);
        return Arrays.asList(accountsArray);
    }

    // Получение аккаунта по ID
    public static Accounts getAccountById(String username, String password, int accountId){
        List<Accounts> accounts = getUSerAccounts(username, password);

        return accounts.stream()
                .filter(account -> account.getId() == accountId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Аккаунт с ID" + accountId + "не найден!"));
    }

    // Получение баланса аккаунта
    public static double getBalanceAccount(String username, String password, int accountId){
        Accounts account = getAccountById(username, password, accountId);
        return account.getBalance();
    }
}
