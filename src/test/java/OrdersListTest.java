import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
public class OrdersListTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Проверяем, что возвращается список заказов")
    public void checkOrderBodyResponseHaveValue() {
        given()
                .log()
                .all()
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body("orders", notNullValue());
    }
}

