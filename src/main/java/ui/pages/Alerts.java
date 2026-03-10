package ui.pages;

import lombok.Getter;

@Getter
public enum Alerts {
    // Profile
    NAME_UPDATED("✅ Name updated successfully!"),
    INVALID_NAME_ERROR("Name must contain two words with letters only"),
    EMPTY_NAME_ERROR("❌ Please enter a valid name."),

    // Deposit
    DEPOSIT_SUCCESS("✅ Successfully deposited $"),
    DEPOSIT_EXCEED_LIMIT("❌ Please deposit less or equal to 5000$."),

    // Transfer
    TRANSFER_SUCCESS("✅ Successfully transferred $"),
    TRANSFER_EXCEED_LIMIT("❌ Error: Transfer amount cannot exceed 10000"),
    CONFIRMATION_REQUIRED("❌ Please fill all fields and confirm.");

    private final String message;

    Alerts(String message) {
        this.message = message;
    }
}
