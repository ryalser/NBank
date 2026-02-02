package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserRequest extends BaseModel {
    // Модель запроса /api/v1/auth/login
    private String username;
    private String password;
}