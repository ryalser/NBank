package api_tests_level_senior.iteration_2;

import constants.Message;
import generators.RandomData;
import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.AdminSteps;
import requests.steps.ProfileSteps;
import specs.RequestsSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

public class ChangeNameUserTest extends BaseTest {

    @Test
    @DisplayName("Успешное изменение имени пользователя")
    public void changeNameTest(){
        CreateUserResponse user  = AdminSteps.createUserAsUser();

        String username = user.getUsername();
        String password = AdminSteps.getOriginalPassword(username);
        String defaultName = user.getName();
        //Формируем рандомное имя
        String newName = RandomData.getName();


        PutCustomerProfileResponse updateResponse = ProfileSteps.updateProfile(username, password, newName);

        softly.assertThat(updateResponse.getMessage())
                .isEqualTo(Message.Success.PROFILE_UPDATED_SUCCESSFULLY);
        softly.assertThat(updateResponse.getCustomer().getName())
                .isEqualTo(newName);
        softly.assertThat(updateResponse.getCustomer().getName())
                .isNotEqualTo(defaultName);

        GetCustomerProfileResponse userProfile = ProfileSteps.getProfile(username,password);

        softly.assertThat(userProfile.getName()).isEqualTo(newName);
        softly.assertThat(userProfile.getName()).isNotEqualTo(defaultName);
    }

    //Создаем набор невалидных данных для негативных тестов
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

        softly.assertThat(defaultName).isNull();// при создании пользователя имя NULL
        softly.assertThat(ProfileSteps.getProfile(
                username,AdminSteps.getOriginalPassword(username))
                .getName()).isNull(); // имя профиля - NULL
    }
}