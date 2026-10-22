package ru.yandex.task2.tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.task2.TestBase;
import ru.yandex.task2.clients.UserClient;
import ru.yandex.task2.models.User;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginUserTest extends TestBase {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    @Step("Подготовка: регистрация пользователя")
    public void init() {
        userClient = new UserClient();
        user = generateRandomUser();
        userClient.register(user);
    }

    @After
    @Step("Очистка: удаление пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Логин существующего пользователя – успех")
    public void loginExistingUserSuccess() {
        var response = userClient.login(user);
        accessToken = response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .extract()
                .path("accessToken");
    }

    @Test
    @DisplayName("Логин с неверным логином/паролем – ошибка 401")
    public void loginInvalidCredentialsFails() {
        User invalidUser = new User(user.getEmail(), "wrongPassword", null);
        userClient.login(invalidUser)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));

    }
}