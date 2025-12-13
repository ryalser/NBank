package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Модель данных для создания пользователя

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest extends BaseModel {
    // Данные пользователя
    private String username;
    private String password;
    private String role;
}
