package models;

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
    private int id;
    private String accountNumber;
    private double balance;
    // Транзакции(массив "transactions") здесь не будем проверять, это будет отдельной логикой
}
