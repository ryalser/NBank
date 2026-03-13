package api.models;

import api.generators.GeneratingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositRequest extends BaseModel {
    // Модель запроса /api/v1/accounts/deposit
    @GeneratingRule(regex = "^[1-9][0-9]{0,3}$")// ID от 1 до 9999
    private int id;
    @GeneratingRule(regex = "^[0-9]{1,4}\\.[0-9]{2}$") // 0.01 до 9999.99
    private double balance;
}