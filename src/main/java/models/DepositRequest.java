package models;

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
    private int id;
    private double balance;
}
