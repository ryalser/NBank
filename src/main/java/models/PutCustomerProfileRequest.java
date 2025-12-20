package models;

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
    private String name;
}
