package constants;

public class Message {
    private Message() {}

    public static class Success{
        private Success(){}

        public static final String PROFILE_UPDATED_SUCCESSFULLY = "Profile updated successfully";
        public static final String TRANSFER_SUCCESSFUL = "Transfer successful";
    }

    public static class Validation {
        private Validation(){};

        public static final String VALIDATION_TWO_WORDS_LETTERS_ONLY = "Name must contain two words with letters only";
        public static final String AMOUNT_TRANSFER_MIN_0_01 = "Transfer amount must be at least 0.01";
        public static final String AMOUNT_TRANSFER_MAX_10000 = "Transfer amount cannot exceed 10000";
        public static final String INVALID_ACCOUNT_OR_INSUFFICIENT_FUNDS = "Invalid transfer: insufficient funds or invalid accounts";
        public static final String DEPOSIT_AMOUNT_MIN_0_01 = "Deposit amount must be at least 0.01";
        public static final String DEPOSIT_AMOUNT_MAX_5000 = "Deposit amount cannot exceed 5000";
    }

    public static class Security{
        private Security(){}
        public static final String UNAUTHORIZED_ACCESS_TO_ACCOUNT = "Unauthorized access to account";

    }

    public static class Business{
        private Business(){}

        public static final String ACCOUNT_NOT_FOUND = "Аккаунт не найден";
    }
}

