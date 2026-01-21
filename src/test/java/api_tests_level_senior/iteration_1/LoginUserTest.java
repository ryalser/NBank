package api_tests_level_senior.iteration_1;

import models.CreateUserResponse;
import models.LoginUserRequest;
import org.junit.jupiter.api.Test;
import requests.LoginUserRequester;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestsSpecs;
import specs.ResponseSpecs;

public class LoginUserTest extends BaseTest {
    @Test
    public void adminCanGenerateAuthToken() {
        LoginUserRequest loginUserRequest = LoginUserRequest.builder()
                .username("admin")
                .password("admin")
                .build();

        new ValidatedCrudRequester<CreateUserResponse>(RequestsSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOk())
                .post(loginUserRequest);
    }
}
