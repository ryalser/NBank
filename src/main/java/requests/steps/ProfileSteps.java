package requests.steps;

import generators.RandomModelGenerator;
import io.restassured.response.ValidatableResponse;
import models.GetCustomerProfileResponse;
import models.PutCustomerProfileRequest;
import models.PutCustomerProfileResponse;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestsSpecs;
import specs.ResponseSpecs;

public class ProfileSteps {
    // Обновление профиля с указанным именем
   public static PutCustomerProfileResponse updateProfile(String username, String password, String newName) {
       PutCustomerProfileRequest profileRequest = PutCustomerProfileRequest.builder()
               .name(newName)
               .build();

       return new ValidatedCrudRequester<PutCustomerProfileResponse>(
               RequestsSpecs.authAsUser(username, password),
               Endpoint.CUSTOMER_PROFILE,
               ResponseSpecs.requestReturnsOk()
       ).update(profileRequest);
   }

   // Обновление профиля со случайным именем
   public static PutCustomerProfileResponse updateProfile(String username, String password){
       PutCustomerProfileRequest profileRequest = RandomModelGenerator.generate(PutCustomerProfileRequest.class);

       ValidatedCrudRequester<PutCustomerProfileResponse> requester = new ValidatedCrudRequester<>(
               RequestsSpecs.authAsUser(username, password),
               Endpoint.CUSTOMER_PROFILE,
               ResponseSpecs.requestReturnsOk()
       );

       return requester.update(profileRequest);
   }

   // Получение профиля пользователя
    public static GetCustomerProfileResponse getProfile (String username, String password){
        return new ValidatedCrudRequester<GetCustomerProfileResponse>(
                RequestsSpecs.authAsUser(username, password),
                Endpoint.GET_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsOk()
        ).get();
    }

    // Проверка имени в профиле c тем, что пришел во входящих параметрах
    public static boolean isNameProfile(String username, String password, String expectedName){
       GetCustomerProfileResponse profile = getProfile(username, password);

       return expectedName.equals(profile.getName());
    }
}