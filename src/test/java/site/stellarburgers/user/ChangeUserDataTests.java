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

public class ChangeUserDataTests {

    private UserClient userClient;
    private User user;
    private String bearerToken;

    @Before
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();
    }

    @Test
    public void changeDataUserWithAuthorization() {
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");

        User secondUser = getRandomUser();

        ValidatableResponse responsePatch = userClient.patch(secondUser, bearerToken);
        responsePatch.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    public void changeDataUserWithoutAuthorization() {
        ValidatableResponse responseRegister = userClient.register(user);

        bearerToken = responseRegister.extract().path("accessToken");

        User secondUser = getRandomUser();

        ValidatableResponse responsePatch = userClient.patch(secondUser, " ");
        responsePatch.assertThat().statusCode(SC_UNAUTHORIZED).body("success", is(false))
                .and().body("message", is("You should be authorised"));
    }


    @After
    public void tearDown() {

        if (bearerToken == null) return;
        userClient.delete(bearerToken);

    }
}

