package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction extends BaseModel {
    // Модель ответа при получении массива транзакций
    private int id;
    private double amount;
    private String type;
    private String timestamp;
    private int relatedAccountId;
}