package ru.yandex.task2.clients;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.yandex.task2.models.User;

import static io.restassured.RestAssured.given;

public class UserClient {

    private static final String REGISTER_PATH = "/api/auth/register";
    private static final String LOGIN_PATH = "/api/auth/login";
    private static final String USER_PATH = "/api/auth/user";

    public Response register(User user) {
        return given()
                .header("Content-Type", "application/json")
                .body(user)
                .post(REGISTER_PATH);
    }

    public Response login(User user) {
        return given()
                .header("Content-Type", "application/json")
                .body(user)
                .post(LOGIN_PATH);
    }

    public Response getUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .get(USER_PATH);
    }

    public Response updateUser(String accessToken, User user) {
        RequestSpecification request = given()
                .header("Content-Type", "application/json")
                .body(user);
        if (accessToken != null) {
            request.header("Authorization", accessToken);
        }
        return request.patch(USER_PATH);
    }

    public Response deleteUser(String accessToken) {
        if (accessToken == null) {
            throw new IllegalArgumentException("Access token cannot be null for deletion");
        }
        return given()
                .header("Authorization", accessToken)
                .delete(USER_PATH);
    }
}