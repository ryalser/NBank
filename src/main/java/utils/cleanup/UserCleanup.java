package utils.cleanup;

import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.AdminSteps;
import specs.RequestsSpecs;
import specs.ResponseSpecs;

import java.util.List;

public class UserCleanup {
    public static void cleanUsers() {
        List<Integer> userIds = AdminSteps.getIdAllUsers();

        if (userIds.isEmpty()) {
            System.out.println("Нет пользователей для удаления.");
            return;
        }

        CrudRequester deleter = new CrudRequester(
                RequestsSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsOk()
        );
        for (Integer id : userIds) {
                deleter.delete(id);
        }
    }
}
