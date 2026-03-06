package api.requests.steps;

import api.generators.RandomModelGenerator;
import io.restassured.response.ValidatableResponse;
import api.models.GetCustomerProfileResponse;
import api.models.PutCustomerProfileRequest;
import api.models.PutCustomerProfileResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestsSpecs;
import api.specs.ResponseSpecs;

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
        ValidatableResponse response = new CrudRequester(
                RequestsSpecs.authAsUser(username, password),
                Endpoint.GET_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsOk()
        ).get();

        return response.extract().as(GetCustomerProfileResponse.class);
    }

    // Проверка имени в профиле c тем,что пришел во входящих параметрах
    public static boolean isNameProfile(String username, String password, String expectedName){
       GetCustomerProfileResponse profile = getProfile(username, password);

       return expectedName.equals(profile.getName());
    }
}