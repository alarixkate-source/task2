package ru.yandex.task2.clients;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class IngredientsClient {

    private static final String INGREDIENTS_PATH = "/api/ingredients";

    public Response getIngredients() {
        return given()
                .get(INGREDIENTS_PATH);
    }
}