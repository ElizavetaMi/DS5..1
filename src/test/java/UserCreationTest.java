import com.codeborne.selenide.Condition;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;

public class UserCreationTest {

    private static RequestSpecification requestSpec;

    @BeforeAll
    static void setUpAll() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(9999)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(io.restassured.filter.log.LogDetail.ALL)
                .build();
    }

    // Открытие страницы перед каждым UI-тестом
    @BeforeEach
    void openLoginPage() {
        open("http://localhost:9999");
    }

    // UI ТЕСТЫ

    @Test
    void shouldLoginWithActiveUser() {
        var user = DataGenerator.createAndRegisterUser("active");

        $("[data-test-id=login] input").setValue(user.getLogin());
        $("[data-test-id=password] input").setValue(user.getPassword());
        $("[data-test-id=action-login]").click();

        $("h2").shouldHave(Condition.text("Личный кабинет")); // проверка успешного входа
    }

    @Test
    void shouldNotLoginWithBlockedUser() {
        var user = DataGenerator.createAndRegisterUser("blocked");

        $("[data-test-id=login] input").setValue(user.getLogin());
        $("[data-test-id=password] input").setValue(user.getPassword());
        $("[data-test-id=action-login]").click();

        $("[data-test-id=error-notification]")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("Ошибка! Пользователь заблокирован"));
    }

    @Test
    void shouldNotLoginWithInvalidPassword() {
        var user = DataGenerator.createAndRegisterUser("active");

        $("[data-test-id=login] input").setValue(user.getLogin());
        $("[data-test-id=password] input").setValue("wrongPass");
        $("[data-test-id=action-login]").click();

        $("[data-test-id=error-notification]").shouldBe(Condition.visible);
    }

    @Test
    void shouldNotLoginWithInvalidLogin() {
        var user = DataGenerator.createAndRegisterUser("active");

        $("[data-test-id=login] input").setValue("wrongLogin");
        $("[data-test-id=password] input").setValue(user.getPassword());
        $("[data-test-id=action-login]").click();

        $("[data-test-id=error-notification]").shouldBe(Condition.visible);
    }

    // API ТЕСТЫ

    @Test
    void createActiveUserTest() {
        given()
                .spec(requestSpec)
                .body(new RegistrationDto("vasya", "password", "active"))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    @Test
    void createBlockedUserTest() {
        given()
                .spec(requestSpec)
                .body(new RegistrationDto("ivan", "password123", "blocked"))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    @Test
    void createUserWithExistingLoginTest() {
        given()
                .spec(requestSpec)
                .body(new RegistrationDto("vasya", "password", "active"))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);

        given()
                .spec(requestSpec)
                .body(new RegistrationDto("vasya", "newpassword", "active"))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    @Test
    void createUserWithInvalidPasswordTest() {
        given()
                .spec(requestSpec)
                .body(new RegistrationDto("newuser", "", "active"))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }
}
