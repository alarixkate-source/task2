package ru.yandex.task2.clients;

import io.restassured.response.Response;
import ru.yandex.task2.models.OrderRequest;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String ORDERS_PATH = "/api/orders";

    public Response createOrder(String accessToken, OrderRequest orderRequest) {
        return given()
                .header("Authorization", accessToken)
                .header("Content-Type", "application/json")
                .body(orderRequest)
                .post(ORDERS_PATH);
    }

    public Response createOrderWithoutAuth(OrderRequest orderRequest) {
        return given()
                .header("Content-Type", "application/json")
                .body(orderRequest)
                .post(ORDERS_PATH);
    }

    public Response getUserOrders(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .get(ORDERS_PATH);
    }

    public Response getUserOrdersWithoutAuth() {
        return given()
                .get(ORDERS_PATH);
    }
}