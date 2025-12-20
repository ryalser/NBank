package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferMoneyResponse extends BaseModel {
    // Модель ответа /api/v1/accounts/transfer
    private double amount;
    private String message;
    private int senderAccountId;
    private int receiverAccountId;
}
