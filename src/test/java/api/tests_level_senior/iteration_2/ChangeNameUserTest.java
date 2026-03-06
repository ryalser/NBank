package api.tests_level_senior.iteration_2;

import api.models.CreateUserResponse;
import api.models.GetCustomerProfileResponse;
import api.models.PutCustomerProfileRequest;
import api.models.PutCustomerProfileResponse;
import constants.api.Message;
import constants.api.TestDataConstants;
import api.generators.RandomData;
import api.models.comparison.ModelAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.ProfileSteps;
import api.specs.RequestsSpecs;
import api.specs.ResponseSpecs;

import java.util.stream.Stream;

public class ChangeNameUserTest extends BaseTest {

    @Test
    @DisplayName("Успешное изменение имени пользователя")
    public void changeNameTest(){
        CreateUserResponse user  = AdminSteps.createUserAsUser();

        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);
        String newName = RandomData.getName();

        PutCustomerProfileRequest profileRequest = PutCustomerProfileRequest.builder()
                .name(newName)
                .build();

        PutCustomerProfileResponse updateResponse = ProfileSteps.updateProfile(username, password, newName);

        softly.assertThat(updateResponse.getMessage())
                .isEqualTo(Message.Success.PROFILE_UPDATED_SUCCESSFULLY);

        ModelAssertions.assertThatModels(
                profileRequest, updateResponse.getCustomer()).match();

        GetCustomerProfileResponse userProfile = ProfileSteps.getProfile(username,password);

        softly.assertThat(userProfile.getName()).isEqualTo(newName);
        softly.assertThat(userProfile.getName()).isNotEqualTo(TestDataConstants.DEFAULT_NAME);
    }

    public static Stream<Arguments> userInvalidData(){
        return Stream.of(
                Arguments.of(RandomData.getNameWithoutSpace(),Message.Validation.VALIDATION_TWO_WORDS_LETTERS_ONLY),
                Arguments.of(RandomData.getNameWithNumber(),Message.Validation.VALIDATION_TWO_WORDS_LETTERS_ONLY),
                Arguments.of(RandomData.getNameWithSpacesOnly(),Message.Validation.VALIDATION_TWO_WORDS_LETTERS_ONLY)
        );
    }

    @MethodSource("userInvalidData")
    @ParameterizedTest
    @DisplayName("Негатив: валидация параметров при изменении имени")
    public void changeWithInvalidNameTest(String name, String errorValue){
        CreateUserResponse user = AdminSteps.createUserAsUser();
        String username = user.getUsername();
        String defaultName = user.getName();

        PutCustomerProfileRequest updateNameRequest = PutCustomerProfileRequest.builder()
                .name(name)
                .build();

        new CrudRequester(
                RequestsSpecs.authAsUser(user.getUsername(),AdminSteps.getOriginalPassword(username)),
                Endpoint.CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsTextBadRequest(errorValue)
                ).update(updateNameRequest);

        softly.assertThat(defaultName).isNull();
        softly.assertThat(ProfileSteps.getProfile(
                username,AdminSteps.getOriginalPassword(username))
                .getName()).isNull();
    }
}