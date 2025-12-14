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
    private int id; // id аккаунта для пополнения
    private double balance; // Сумма пополнения
}
