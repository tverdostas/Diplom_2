package site.stellarburgers.order;

import site.stellarburgers.data.Order;


public class OrderGenerator {
    public static Order getListOrder() {

        return new Order()
                .setIngredients(OrderClient.getAllIngredients().extract().path("data._id"));
    }
}
