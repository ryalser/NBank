package utils.cleanup;

import java.util.List;

import static io.restassured.RestAssured.given;

public class UserCleanup {
    public static void cleanUsers() {
        // Собирается список id пользователей
        List<Integer> userId = given()
                .header("Authorization","Basic YWRtaW46YWRtaW4=")
                .get("http://localhost:4111/api/v1/admin/users")
                .then()
                .extract()
                .jsonPath()
                .getList("id");

        for(Integer id : userId){
            if(id == null){
                System.out.println("Список юзеров пуст.");
            }
            given()
                    .header("Authorization","Basic YWRtaW46YWRtaW4=")
                    .delete("http://localhost:4111/api/v1/admin/users/" + id);
        }
    }
}
