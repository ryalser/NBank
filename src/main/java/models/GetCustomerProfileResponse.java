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
public class GetCustomerProfileResponse extends BaseModel {
    // Модель ответа GET /api/v1/customer/profile
    private int id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<Accounts> accounts;
}