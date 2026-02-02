package models;

import generators.GeneratingRule;
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
    @GeneratingRule(regex = "^[1-9][0-9]{0,3}$")
    private int senderAccountId;
    @GeneratingRule(regex = "^[1-9][0-9]{0,3}$")
    private int receiverAccountId;
    @GeneratingRule(regex = "^[0-9]{1,4}\\.[0-9]{2}$") // 0.01 до 9999.99
    private double amount;
}