package requests.skelethon.requesters;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;
import requests.skelethon.Endpoint;
import requests.skelethon.HttpRequest;
import requests.skelethon.interfaces.CrudEndpointInterface;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEndpointInterface {

    public CrudRequester(RequestSpecification requestSpecification,
                         Endpoint endpoint,
                         ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public ValidatableResponse post(BaseModel model) {
        var body = model == null ? "" : model;

        return given()
                .spec(requestSpecification)
                .body(body)
                .post(endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse get(long id) {
        String url = endpoint.getUrl();
        if(id > 0){
            url = url + "/" + id;
        }

        return given()
                .spec(requestSpecification)
                .get(url)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    public ValidatableResponse get() {

        return given()
                .spec(requestSpecification)
                .get(endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse update(long id, BaseModel model) {
        var body = model == null ? "" : model;
        // Для PUT запросов (update)
        return given()
                .spec(requestSpecification)
                .body(body)
                .put(endpoint.getUrl()) // Используем PUT метод
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    public ValidatableResponse update(BaseModel model){
        return update(0,model);
    }

    @Override
    public ValidatableResponse delete(long id) {
         String url = endpoint.getUrl();
         if(id > 0){
             url = url + "/" + id;
         }

        return given()
                .spec(requestSpecification)
                .delete(url)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}