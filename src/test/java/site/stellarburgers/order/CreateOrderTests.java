package site.stellarburgers.order;

import site.stellarburgers.data.Order;
import site.stellarburgers.data.User;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.stellarburgers.user.UserClient;

import java.util.ArrayList;
import java.util.List;

import static site.stellarburgers.order.OrderGenerator.getListOrder;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;
import static site.stellarburgers.user.UserGenerator.getRandomUser;

public class CreateOrderTests {

    private UserClient userClient;
    private User user;
    private OrderClient orderClient;
    private Order order;

    private String bearerToken;

    @Before
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();
        order = getListOrder();
        orderClient = new OrderClient();
    }

    @Test
    public void createOrderWithAuthorizationTest() {
        ValidatableResponse responseRegister = userClient.register(user);
        userClient.login(user);
        bearerToken = responseRegister.extract().path("accessToken");
        ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);

        responseCreateOrder.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    public void createOrderWithoutAuthorizationTest() {
        bearerToken = "";
        ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);

        responseCreateOrder.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    public void createOrderWithoutIngridientTest() {
        ValidatableResponse responseRegister = userClient.register(user);
        userClient.login(user);
        bearerToken = responseRegister.extract().path("accessToken");

        order.setIngredients(java.util.Collections.emptyList());

        ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);

        responseCreateOrder.assertThat().statusCode(SC_BAD_REQUEST).body("success", is(false)).and().body("message", is("Ingredient ids must be provided"));
    }

    @Test
    public void createOrderWithWrongIngridientTest() {
        ValidatableResponse responseRegister = userClient.register(user);
        userClient.login(user);
        bearerToken = responseRegister.extract().path("accessToken");

        List wrongIngridient = new ArrayList();
        wrongIngridient.add("609646e4dc916e00276b2870");

        order.setIngredients(wrongIngridient);

        ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);

        responseCreateOrder.assertThat().statusCode(SC_BAD_REQUEST).body("success", is(false)).and().body("message", is("One or more ids provided are incorrect"));
    }


    @After
    public void tearDown() {

        if (bearerToken.equals("")) return;
        userClient.delete(bearerToken);

    }
}
