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
public class CreateUserRequest extends BaseModel {
    // Модель запроса /api/v1/admin/users
    @GeneratingRule(regex = "^[A-Za-z0-9]{3,15}$")
    private String username;
    @GeneratingRule(regex = "^[A-Z]{3}[a-z]{5}[0-9]{4}\\$$")
    private String password;
    @GeneratingRule(regex = "^(USER|ADMIN)$")
    private String role;
}