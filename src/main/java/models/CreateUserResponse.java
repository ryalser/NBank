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
public class CreateUserResponse extends BaseModel {
    // Модель ответа /api/v1/admin/users
    private int id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<String> accounts;
}
