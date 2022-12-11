import Models.Order;
import Models.OrderTrack;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    Order order;
    OrderTrack orderTrack;
    Response response;
    String orderMainLink = "/api/v1/orders";
    private final List<String> colors;

    public CreateOrderTest (List<String> color) {
        this.colors = color;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        order = new Order("Naruto", "Uchiha", "Konoha", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", colors);
        response = given().header("Content-type", "application/json").body(order).post(orderMainLink);
        orderTrack = response.body().as(OrderTrack.class);
    }

    @Parameterized.Parameters
    public static Object[][] getColors() {
        return new Object[][] {
                {Arrays.asList("BLACK", "GREY")},
                {Arrays.asList("BLACK")},
                {Arrays.asList("GREY")},
                {Arrays.asList("")},
        };
    }

    @Step("Создаём заказ")
    public Response createOrder() {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post(orderMainLink);
    }

    @Test
    @DisplayName("Проверяем, что можно создать заказ c указанием цвета {0}, {1}")
    public void checkStatusCodeSuccessCreateOrder() {
        createOrder()
                .then()
                .statusCode(201)
                .and()
                .assertThat()
                .body("track", notNullValue());
    }

    @After
    public void cancelOrder() {
        given()
                .header("Content-type", "application/json")
                .body(orderTrack)
                .put("/api/v1/orders/cancel/?track=" + orderTrack.getOrderTrack());
    }
}
