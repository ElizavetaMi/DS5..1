import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.*;

public class UserCreationTest {

    @BeforeEach
    void openLoginPage() {
        open("http://localhost:9999");
    }

    @Test
    void shouldLoginWithActiveUser() {
        var user = DataGenerator.createAndRegisterUser("active");

        $("[data-test-id=login] input").setValue(user.getLogin());
        $("[data-test-id=password] input").setValue(user.getPassword());
        $("[data-test-id=action-login]").click();

        $("h2").shouldHave(Condition.text("Личный кабинет"));
    }

    @Test
    void shouldNotLoginWithBlockedUser() {
        var user = DataGenerator.getBlockedUser();
        DataGenerator.sendCreateUserRequest(user);

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

        String invalidPassword = user.getPassword() + "_wrong";

        $("[data-test-id=login] input").setValue(user.getLogin());
        $("[data-test-id=password] input").setValue(invalidPassword);
        $("[data-test-id=action-login]").click();

        $("[data-test-id=error-notification]").shouldBe(Condition.visible);
    }

    @Test
    void shouldNotLoginWithInvalidLogin() {
        var user = DataGenerator.createAndRegisterUser("active");

        String invalidLogin = user.getLogin() + "_invalid";

        $("[data-test-id=login] input").setValue(invalidLogin);
        $("[data-test-id=password] input").setValue(user.getPassword());
        $("[data-test-id=action-login]").click();

        $("[data-test-id=error-notification]").shouldBe(Condition.visible);
    }
}