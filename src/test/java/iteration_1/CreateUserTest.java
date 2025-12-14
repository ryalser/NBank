package iteration_1;

import generators.RandomData;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.UserRole;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
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

        CreateUserResponse createUserResponse = new AdminCreateUserRequester(RequestsSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(createUserRequest).extract().as(CreateUserResponse.class);

        softly.assertThat(createUserRequest.getUsername()).isEqualTo(createUserResponse.getUsername());
        softly.assertThat(createUserRequest.getRole()).isEqualTo(createUserResponse.getRole());
        }
    }

