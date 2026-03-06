package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PutCustomerProfileResponse extends BaseModel {
    // Модель ответа PUT /api/v1/customer/profile
    private String message;
    private Customers customer;
}