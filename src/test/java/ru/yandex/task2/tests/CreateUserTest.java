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

public class CreateUserTest extends TestBase {

    private UserClient userClient;
    private User user;
    private String accessToken; // сохраняем accessToken для удаления

    @Before
    @Step("Подготовка: генерация уникального пользователя")
    public void init() {
        userClient = new UserClient();
        user = generateRandomUser();
    }

    @After
    @Step("Очистка: удаление пользователя (если был создан)")
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя – успех")
    public void createUniqueUserSuccess() {
        var response = userClient.register(user);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
        // Сохраняем accessToken для удаления
        accessToken = response.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Создание уже существующего пользователя – ошибка 403")
    public void createExistingUserFails() {
        var response = userClient.register(user);
        response.then().statusCode(200);
        accessToken = response.then().extract().path("accessToken");

        // Пытаемся создать такого же
        userClient.register(user)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email – ошибка 403")
    public void createUserWithoutEmailFails() {
        User incompleteUser = new User(null, user.getPassword(), user.getName());
        userClient.register(incompleteUser)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без password – ошибка 403")
    public void createUserWithoutPasswordFails() {
        User incompleteUser = new User(user.getEmail(), null, user.getName());
        userClient.register(incompleteUser)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без name – ошибка 403")
    public void createUserWithoutNameFails() {
        User incompleteUser = new User(user.getEmail(), user.getPassword(), null);
        userClient.register(incompleteUser)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}