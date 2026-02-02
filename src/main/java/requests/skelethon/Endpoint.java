package requests.skelethon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.*;

@Getter
@AllArgsConstructor
public enum Endpoint {
    //Admin
    ADMIN_USER(
            "/admin/users",
            CreateUserRequest.class,
            CreateUserResponse.class
    ),
    GET_ADMIN_USER(
            "/admin/users",
            null,
            GetAllUsers.class
    ),
    //Accounts
    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            UserCreateAccountResponse.class

    ),
    DEPOSIT(
            "/accounts/deposit",
            DepositRequest.class,
            DepositResponse.class
    ),
    TRANSFER(
            "/accounts/transfer",
            TransferMoneyRequest.class,
            TransferMoneyResponse.class
    ),
    //Authentication
    LOGIN(
            "/auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class
    ),
    //Customer
    CUSTOMER_PROFILE(
            "/customer/profile",
            PutCustomerProfileRequest.class,
            PutCustomerProfileResponse.class
    ),
    GET_CUSTOMER_PROFILE(
            "/customer/profile",
            null,
            GetCustomerProfileResponse.class
    ),
    CUSTOMER_ACCOUNTS(
            "/customer/accounts",
            null,
            GetUserAccountsResponse.class
    ),
    CREATE_ACCOUNT(
            "/accounts",
            null,
            UserCreateAccountResponse.class
    );


    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}