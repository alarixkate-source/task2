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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateOrderTest extends TestBase {

    private UserClient userClient;
    private OrderClient orderClient;
    private IngredientsClient ingredientsClient;
    private User user;
    private String accessToken;
    private List<String> ingredientIds;

    @Before
    @Step("Подготовка: регистрация пользователя, получение токенов и списка ингредиентов")
    public void init() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        ingredientsClient = new IngredientsClient();

        user = generateRandomUser();
        userClient.register(user);
        var loginResponse = userClient.login(user);
        accessToken = loginResponse.then().extract().path("accessToken");

        ingredientIds = ingredientsClient.getIngredients()
                .then()
                .extract()
                .jsonPath()
                .getList("data._id", String.class)
                .stream()
                .limit(2)
                .collect(Collectors.toList());
    }

    @After
    @Step("Очистка: удаление пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами – успех")
    public void createOrderWithAuthSuccess() {
        OrderRequest orderRequest = new OrderRequest(ingredientIds);
        orderClient.createOrder(accessToken, orderRequest)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("name", notNullValue());
    }
    // ну допустим)) странноватое, но окей. В документации не сказано, что должна быть ошибка. Но я бы у аналитика уточнила этот момент, конечно.
    @Test
    @DisplayName("Создание заказа без авторизации – успех (API позволяет)")
    public void createOrderWithoutAuthSuccess() {
        OrderRequest orderRequest = new OrderRequest(ingredientIds);
        orderClient.createOrderWithoutAuth(orderRequest)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов – ошибка 400")
    public void createOrderWithoutIngredientsFails() {
        OrderRequest emptyOrder = new OrderRequest(Arrays.asList());
        orderClient.createOrder(accessToken, emptyOrder)
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }
// не проверяю тут тело, тут код html страницы, переживем без него)
    @Test
    @DisplayName("Создание заказа с невалидным хешем ингредиента (не ObjectId) – ошибка 500")
    public void createOrderInvalidHashFails() {
        List<String> invalidIds = Arrays.asList("1", "1");
        OrderRequest invalidOrder = new OrderRequest(invalidIds);
        orderClient.createOrder(accessToken, invalidOrder)
                .then()
                .statusCode(500);
    }
}