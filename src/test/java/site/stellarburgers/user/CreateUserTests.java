package site.stellarburgers.user;

import io.qameta.allure.junit4.DisplayName;
import site.stellarburgers.data.User;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;
import static site.stellarburgers.user.UserGenerator.getRandomUser;

public class CreateUserTests {

    private UserClient userClient;
    private User user;
    private String bearerToken;

    @Before
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Успешное создание юзера")
    public void createUserTest() {
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");
        responseRegister.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @DisplayName("Получение ошибки при создании уже существующего юзера")
    public void createAlreadyExistsUserTest() {
        ValidatableResponse responseRegisterFirstUser = userClient.register(user);
        bearerToken = responseRegisterFirstUser.extract().path("accessToken");

        ValidatableResponse responseRegisterSecondUser = userClient.register(user);
        responseRegisterSecondUser.assertThat().statusCode(SC_FORBIDDEN).body("success", is(false)).body("message", is("User already exists"));
    }

    @Test
    @DisplayName("Получение ошибки при создании юзера без имени")
    public void createUserWithoutNameTest() {
        user.setName("");
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");
        responseRegister.assertThat().statusCode(SC_FORBIDDEN).body("success", is(false)).body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Получение ошибки при создании юзера без email")
    public void createUserWithoutEmailTest() {
        user.setEmail("");
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");
        responseRegister.assertThat().statusCode(SC_FORBIDDEN).body("success", is(false)).body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Получение ошибки при создании юзера без пароля")
    public void createUserWithoutPasswordTest() {
        user.setPassword("");
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");
        responseRegister.assertThat().statusCode(SC_FORBIDDEN).body("success", is(false)).body("message", is("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {

        if (bearerToken == null) return;
        userClient.delete(bearerToken);

    }
}
