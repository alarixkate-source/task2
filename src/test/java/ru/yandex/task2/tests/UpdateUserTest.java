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

public class UpdateUserTest extends TestBase {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    @Step("Подготовка: регистрация и логин пользователя")
    public void init() {
        userClient = new UserClient();
        user = generateRandomUser();
        userClient.register(user);
        var loginResponse = userClient.login(user);
        accessToken = loginResponse.then().extract().path("accessToken");
    }

    @After
    @Step("Очистка: удаление пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    // Тесты с авторизацией (изменение каждого поля + проверка сохранения)
    @Test
    @DisplayName("Изменение email с авторизацией – успех, проверка сохранения")
    public void updateEmailWithAuthSuccess() {
        String newEmail = "new_" + user.getEmail();
        User updatedUser = new User(newEmail, user.getPassword(), user.getName());
        userClient.updateUser(accessToken, updatedUser)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(user.getName()));

        // Проверяем, что изменения сохранились через GET-запрос
        userClient.getUser(accessToken)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Изменение password с авторизацией – успех (проверка через логин)")
    public void updatePasswordWithAuthSuccess() {
        String newPassword = "newPassword123";
        User updatedUser = new User(user.getEmail(), newPassword, user.getName());
        userClient.updateUser(accessToken, updatedUser)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));

        // Дополнительная проверка: логинимся с новым паролем
        User loginUser = new User(user.getEmail(), newPassword, user.getName());
        userClient.login(loginUser)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Изменение name с авторизацией – успех, проверка сохранения")
    public void updateNameWithAuthSuccess() {
        String newName = "NewName";
        User updatedUser = new User(user.getEmail(), user.getPassword(), newName);
        userClient.updateUser(accessToken, updatedUser)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(newName));

        // Проверяем, что изменения сохранились через GET-запрос
        userClient.getUser(accessToken)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(newName));
    }

    // Тесты без авторизации (попытка изменить каждое поле – ошибка 401)

    @Test
    @DisplayName("Изменение email без авторизации – ошибка 401")
    public void updateEmailWithoutAuthFails() {
        User updatedUser = new User("new_" + user.getEmail(), user.getPassword(), user.getName());
        userClient.updateUser(null, updatedUser)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение password без авторизации – ошибка 401")
    public void updatePasswordWithoutAuthFails() {
        User updatedUser = new User(user.getEmail(), "newPassword123", user.getName());
        userClient.updateUser(null, updatedUser)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение name без авторизации – ошибка 401")
    public void updateNameWithoutAuthFails() {
        User updatedUser = new User(user.getEmail(), user.getPassword(), "NewName");
        userClient.updateUser(null, updatedUser)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}