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
public class GetUserAccountsResponse extends BaseModel {
    // Модель ответа /api/v1/customer/accounts
    private int id;
    private String accountNumber;
    private double balance;
    private List<Transaction> transactions;
}