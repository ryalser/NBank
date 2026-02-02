package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserResponse extends BaseModel {
    // Модель запроса /api/v1/auth/login
    private String username;
    private String role;
}