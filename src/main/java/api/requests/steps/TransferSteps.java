package api.requests.steps;

import api.generators.RandomModelGenerator;
import api.models.TransferMoneyRequest;
import api.models.TransferMoneyResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestsSpecs;
import api.specs.ResponseSpecs;

public class TransferSteps {
    public static TransferMoneyResponse transferBetweenAccounts(String username, String password, int senderAccountId,
                                                                int receiverAccountId, double amount){
        TransferMoneyRequest transferRequest = TransferMoneyRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(amount)
                .build();

        return new ValidatedCrudRequester<TransferMoneyResponse>(
                RequestsSpecs.authAsUser(username,password),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOk()
        ).post(transferRequest);
    }

    public static TransferMoneyResponse transferBetweenAccount(String username, String password, int senderAccountId,
                                                               int receiverAccountId){
        TransferMoneyRequest transferRequest = RandomModelGenerator.generate(TransferMoneyRequest.class);
        transferRequest.setSenderAccountId(senderAccountId);
        transferRequest.setReceiverAccountId(receiverAccountId);

        return new ValidatedCrudRequester<TransferMoneyResponse>(
                RequestsSpecs.authAsUser(username, password),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOk()
        ).post(transferRequest);
    }

    public static TransferMoneyRequest generateTransferRequest(int senderAccountId, int receiverAccountId){
        TransferMoneyRequest request = RandomModelGenerator.generate(TransferMoneyRequest.class);

        request.setSenderAccountId(senderAccountId);
        request.setReceiverAccountId(receiverAccountId);
        return request;
    }

    public static void transferWithInvalidAmount(String username, String password, int senderAccountId,
                                                 int receiverAccountId,double invalidAmount,String expectedError){
         TransferMoneyRequest transferRequest = TransferMoneyRequest.builder()
                 .senderAccountId(senderAccountId)
                 .receiverAccountId(receiverAccountId)
                 .amount(invalidAmount)
                 .build();

         new CrudRequester(RequestsSpecs.authAsUser(username,password),
                 Endpoint.TRANSFER,
                 ResponseSpecs.requestReturnsTextBadRequest(expectedError)
         ).post(transferRequest);
    }

    public static void transferToInvalidAccount(String username, String password, int senderAccountId,
                                                int invalidReceiverAccountId, double amount, String expectedError){
        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(invalidReceiverAccountId)
                .amount(amount)
                .build();

        new CrudRequester(RequestsSpecs.authAsUser(username, password),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequest(expectedError)
        ).post(transferMoneyRequest);
    }
}