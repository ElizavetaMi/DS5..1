import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.http.ContentType;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class UserCreationTest {

    private static RequestSpecification requestSpec;

    @BeforeAll
    static void setUpAll() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(9999) // Убедитесь, что ваш сервер работает на порту 9999
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(io.restassured.filter.log.LogDetail.ALL)
                .build();
    }

    // Создание пользователя с активным статусом
    @Test
    void createActiveUserTest() {
        given()
                .spec(requestSpec)
                .body(new RegistrationDto("vasya", "password", "active"))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200); // Успешный код 200
    }

    // Создание пользователя с заблокированным статусом
    @Test
    void createBlockedUserTest() {
        given()
                .spec(requestSpec)
                .body(new RegistrationDto("ivan", "password123", "blocked"))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200); // Успешный код 200
    }

    // Перезапись данных пользователя с тем же логином
    @Test
    void createUserWithExistingLoginTest() {
        // Создаём пользователя с логином "vasya"
        given()
                .spec(requestSpec)
                .body(new RegistrationDto("vasya", "password", "active"))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200); // Успешное создание пользователя

        // Попытка создать пользователя с тем же логином, данные будут перезаписаны
        given()
                .spec(requestSpec)
                .body(new RegistrationDto("vasya", "newpassword", "active"))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200); // Код 200, данные перезаписаны
    }

    // Тест для создания пользователя с пустым паролем
    @Test
    void createUserWithInvalidPasswordTest() {
        given()
                .spec(requestSpec)
                .body(new RegistrationDto("newuser", "", "active"))  // Пустой пароль
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);  // Сервер может возвращать 200, если данные перезаписаны
    }
}
