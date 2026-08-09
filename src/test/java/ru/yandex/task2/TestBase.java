package ru.yandex.task2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.BeforeClass;
import ru.yandex.task2.models.User;

public class TestBase {

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    protected User generateRandomUser() {
        long timestamp = System.currentTimeMillis();
        return new User(
                "test_" + timestamp + "@yandex.ru",
                "password" + timestamp,
                "User" + timestamp
        );
    }
}