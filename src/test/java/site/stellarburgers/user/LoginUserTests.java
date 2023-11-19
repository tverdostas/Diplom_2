package site.stellarburgers.user;

import site.stellarburgers.data.User;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static site.stellarburgers.user.UserGenerator.getRandomUser;

public class LoginUserTests {

    private UserClient userClient;
    private User user;
    private String bearerToken;

    private ValidatableResponse responseRegister;

    @Before
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();
        responseRegister = userClient.register(user);
    }

    @Test
    public void loginUser() {
        bearerToken = responseRegister.extract().path("accessToken");

        ValidatableResponse responseLogin = userClient.login(user);
        responseLogin.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    public void loginUserWithWrongPass() {
        bearerToken = responseRegister.extract().path("accessToken");

        user.setPassword("");
        ValidatableResponse responseLogin = userClient.login(user);
        responseLogin.assertThat().statusCode(SC_UNAUTHORIZED).body("success", is(false)).body("message", is("email or password are incorrect"));
    }

    @Test
    public void loginUserWithWrongEmail() {
        bearerToken = responseRegister.extract().path("accessToken");

        user.setEmail("");
        ValidatableResponse responseLogin = userClient.login(user);
        responseLogin.assertThat().statusCode(SC_UNAUTHORIZED).body("success", is(false)).body("message", is("email or password are incorrect"));
    }

    @After
    public void tearDown() {

        if (bearerToken == null) return;
        userClient.delete(bearerToken);

    }
}
