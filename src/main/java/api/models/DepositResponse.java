package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositResponse extends BaseModel {
    // Модель ответа /api/v1/accounts/deposit
    private int id;
    private String accountNumber;
    private double balance;
    private List<Transaction> transactions;
}
