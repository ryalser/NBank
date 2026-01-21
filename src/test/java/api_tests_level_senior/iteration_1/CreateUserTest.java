package api_tests_level_senior.iteration_1;

import generators.RandomData;
import io.restassured.specification.RequestSpecification;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.UserRole;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestsSpecs;
import specs.ResponseSpecs;

public class CreateUserTest extends BaseTest {
    @Test
    public void adminCanCreateUserWithCorrectData() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        CreateUserResponse createUserResponse = new ValidatedCrudRequester<CreateUserResponse>(
                RequestsSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated())
                .post(createUserRequest);


        softly.assertThat(createUserRequest.getUsername()).isEqualTo(createUserResponse.getUsername());
        softly.assertThat(createUserRequest.getPassword()).isNotEqualTo((createUserResponse.getPassword()));
        softly.assertThat(createUserRequest.getRole()).isEqualTo(createUserResponse.getRole());
    }
}
