package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferMoneyRequest extends BaseModel {
    // Модель запроса /api/v1/accounts/transfer
    private int senderAccountId;
    private int receiverAccountId;
    private double amount;
}
