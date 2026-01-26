package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public class ResponseSpecs {
    private ResponseSpecs() {}

    // success message
    public static final String PROFILE_UPDATED_SUCCESSFULLY = "Profile updated successfully";
    public static final String TRANSFER_SUCCESSFUL = "Transfer successful";
    // error message
    public static final String VALIDATION_TWO_WORDS_LETTERS_ONLY = "Name must contain two words with letters only";
    public static final String AMOUNT_TRANSFER_MIN_0_01 = "Transfer amount must be at least 0.01";
    public static final String AMOUNT_TRANSFER_MAX_10000 = "Transfer amount cannot exceed 10000";
    public static final String INVALID_ACCOUNT_OR_INSUFFICIENT_FUNDS = "Invalid transfer: insufficient funds or invalid accounts";
    public static final String DEPOSIT_AMOUNT_MIN_0_01 = "Deposit amount must be at least 0.01";
    public static final String DEPOSIT_AMOUNT_MAX_5000 = "Deposit amount cannot exceed 5000";
    public static final String UNAUTHORIZED_ACCESS_TO_ACCOUNT = "Unauthorized access to account";
    // test constants
    public static final double DEFAULT_ACCOUNT_BALANCE = 0.0;
    public static final String ACCOUNT_NOT_FOUND = "Аккаунт не найден";



    private static ResponseSpecBuilder defaultResponseBuilder() {
        return new ResponseSpecBuilder();
    }

    public static ResponseSpecification entityWasCreated() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }

    public static ResponseSpecification requestReturnsOk() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification requestReturnsBadRequest(String errorKey, String errorValue) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(errorKey, Matchers.equalTo(errorValue))
                .build();
    }

    public static ResponseSpecification requestReturnsTextBadRequest(String errorValue) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(Matchers.equalTo(errorValue))
                .build();
    }

    public static ResponseSpecification requestReturnsTextForbidden(String errorValue) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .expectBody(Matchers.equalTo(errorValue))
                .build();
    }
}
