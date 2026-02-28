package constants.ui;

public class UiMessages {
    private UiMessages() {
    }

    public static class Welcome {  // <- Добавить этот класс
        private Welcome() {
        }

        public static final String DEFAULT_GREETING = "Welcome, noname!";
    }

        public static class Success {
        private Success() {
        }

        public static final String NAME_UPDATED = "✅ Name updated successfully!";
        public static final String PROFILE_UPDATED = "✅ Profile updated successfully";
        public static final String TRANSFER_SUCCESSFUL = "✅ Transfer successful";
    }

    public static class Error {
        private Error() {
        }

        // Profile
        public static final String INVALID_NAME = "Name must contain two words with letters only";
        public static final String EMPTY_NAME = "❌ Please enter a valid name.";
        public static final String INVALID_AMOUNT_MIN = "Transfer amount must be at least 0.01";
        public static final String INVALID_AMOUNT_MAX = "Transfer amount cannot exceed 10000";
        public static final String INSUFFICIENT_FUNDS = "Invalid transfer: insufficient funds or invalid accounts";

        // Deposit
        public static final String DEPOSIT_EXCEED_LIMIT = "❌ Please deposit less or equal to 5000$.";
        public static final String DEPOSIT_INVALID_AMOUNT = "❌ Please enter a valid amount.";
        public static final String DEPOSIT_NO_ACCOUNT_SELECTED = "❌ Please select an account.";
    }
}
