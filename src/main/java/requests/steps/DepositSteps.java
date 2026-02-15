package requests.steps;

import generators.RandomModelGenerator;
import models.DepositRequest;
import models.DepositResponse;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestsSpecs;
import specs.ResponseSpecs;

public class DepositSteps {
    // Пополнение аккаунта с указанием суммы
    public static DepositResponse depositToAccount(String username, String password, int accountId, double amount) {
        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId)
                .balance(amount)
                .build();

        return new ValidatedCrudRequester<DepositResponse>(
                RequestsSpecs.authAsUser(username, password),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOk()
        ).post(depositRequest);
    }

    // Пополнение аккаунта с рандомной(валидной) суммой
    public static DepositResponse depositToAccount(String username, String password, int accountId) {
        DepositRequest depositRequest = RandomModelGenerator.generate(DepositRequest.class); // рандомная сумма
        depositRequest.setId(accountId);

        return new ValidatedCrudRequester<DepositResponse>(
                RequestsSpecs.authAsUser(username, password),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOk()
        ).post(depositRequest);
    }

    // Пополнение аккаунта с рандомной(НЕвалидной) суммой
    public static void depositWithInvalidAmount(String username, String password, int accountId, double invalidAmount,
                                                String expectedError) {
        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId)
                .balance(invalidAmount)
                .build();

        new CrudRequester(
                RequestsSpecs.authAsUser(username, password),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsTextBadRequest(expectedError)
        ).post(depositRequest);
    }

    // Пополнение аккаунта с невалидным аккаунтом(Id)
    public static void depositToInvalidAccount(String username, String password, int invalidAccountId, double amount,
                                               String expectedError) {
        DepositRequest depositRequest = DepositRequest.builder()
                .id(invalidAccountId)
                .balance(amount)
                .build();

        new CrudRequester(
                RequestsSpecs.authAsUser(username, password),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsTextForbidden(expectedError)
        ).post(depositRequest);
    }
}