package models;

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
    private String username;
    private String password;
    private String role;
}
