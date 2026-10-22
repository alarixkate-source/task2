package ru.yandex.task2.tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.task2.TestBase;
import ru.yandex.task2.clients.IngredientsClient;
import ru.yandex.task2.clients.OrderClient;
import ru.yandex.task2.clients.UserClient;
import ru.yandex.task2.models.OrderRequest;
import ru.yandex.task2.models.User;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetOrdersTest extends TestBase {

    private UserClient userClient;
    private OrderClient orderClient;
    private IngredientsClient ingredientsClient;
    private User user;
    private String accessToken;

    @Before
    @Step("Подготовка: регистрация, создание хотя бы одного заказа")
    public void init() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        ingredientsClient = new IngredientsClient();

        user = generateRandomUser();
        userClient.register(user);
        var loginResponse = userClient.login(user);
        accessToken = loginResponse.then().extract().path("accessToken");

        List<String> ingredientIds = ingredientsClient.getIngredients()
                .then()
                .extract()
                .jsonPath()
                .getList("data._id", String.class)
                .stream()
                .limit(2)
                .collect(Collectors.toList());
        OrderRequest orderRequest = new OrderRequest(ingredientIds);
        orderClient.createOrder(accessToken, orderRequest);
    }

    @After
    @Step("Очистка: удаление пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя – успех")
    public void getOrdersWithAuthSuccess() {
        orderClient.getUserOrders(accessToken)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue())
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов без авторизации – ошибка 401")
    public void getOrdersWithoutAuthFails() {
        orderClient.getUserOrdersWithoutAuth()
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}