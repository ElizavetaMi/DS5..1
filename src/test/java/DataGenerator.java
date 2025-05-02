import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class DataGenerator {

    private static final Faker faker = new Faker();

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static RegistrationDto createUser(String status) {
        String login = faker.name().username();
        String password = faker.internet().password();
        return new RegistrationDto(login, password, status);
    }

    public static RegistrationDto getActiveUser() {
        return new RegistrationDto("vasya", "password", "active");
    }

    public static RegistrationDto getBlockedUser() {
        return new RegistrationDto("ivan", "password123", "blocked");
    }

    public static RegistrationDto getUserWithInvalidPassword() {
        return new RegistrationDto("newuser", "wrongpassword", "active");
    }

    public static RegistrationDto getUserWithInvalidLogin() {
        return new RegistrationDto("invalid_login", "password123", "active");
    }

    public static void sendCreateUserRequest(RegistrationDto user) {
        given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    public static RegistrationDto createAndRegisterUser(String status) {
        RegistrationDto user = createUser(status);
        sendCreateUserRequest(user);
        return user;
    }
}
