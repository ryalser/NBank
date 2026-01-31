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
public class PutCustomerProfileRequest extends BaseModel {
    // Модель запроса PUT /api/v1/customer/profile
    @GeneratingRule(regex = "^[A-Z][a-z]{3,10} [A-Z][a-z]{3,10}$") // Два слова с заглавными буквами
    private String name;
}
