package site.stellarburgers.order;

import site.stellarburgers.data.Order;
import site.stellarburgers.data.User;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.stellarburgers.user.UserClient;

import static site.stellarburgers.order.OrderGenerator.getListOrder;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static site.stellarburgers.user.UserGenerator.getRandomUser;

public class GetOrderTests {

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
        bearerToken = responseRegister.extract().path("accessToken");
        userClient.login(user);
        orderClient.create(order, bearerToken);

        ValidatableResponse responseOrderUser = orderClient.getClientOrder(bearerToken);

        responseOrderUser.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    public void createOrderWithoutAuthorizationTest() {
        bearerToken = "";
        ValidatableResponse getClientOrder = orderClient.getClientOrder(bearerToken);

        getClientOrder.assertThat().statusCode(SC_UNAUTHORIZED).body("success", is(false)).and()
                .body("message", is("You should be authorised"));
    }


    @After
    public void tearDown() {

        if (bearerToken.equals("")) return;
        userClient.delete(bearerToken);

    }
}
