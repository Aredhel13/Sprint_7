import Models.Courier;
import Models.CourierID;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {
    Courier courier;
    CourierID courierID;
    Response response;
    String courierLoginLink = "/api/v1/courier/login";
    String courierMainLink = "/api/v1/courier";

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        courier = new Courier("courier130", "qwerty");
        //Создание курьера
        response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(courierMainLink);
    }

    @Step("Авторизация курьера")
    public Response loginCourier() {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(courierLoginLink);
    }

    @Test
    @DisplayName("Проверяем код и тело ответа при успешном логине курьера")
    public void checkLoginCourier() {
        response
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Проверяем, что необходимо заполнять обязательное поле: логин")
    public void checkStatusCodeLoginWithoutLogin() {
        courier.setLogin("");
        loginCourier()
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Проверяем, что необходимо заполнять обязательное поле: пароль")
    public void checkStatusCodeLoginWithoutPassword() {
        courier.setPassword("");
        loginCourier()
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Проверяем, что необходимо заполнять обязательное поле: логин")
    public void checkStatusCodeLoginWithNullLogin() {
        courier.setLogin(null);
        loginCourier()
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Проверяем, что необходимо заполнять обязательное поле: пароль")
    public void checkStatusCodeLoginWithNullPassword() {
        courier.setPassword(null);
        loginCourier()
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
    }
    @Test
    @DisplayName("Проверяем, что авторизация неуспешна при передаче несуществующего логина")
    public void checkStatusCodeLoginWithIncorrectLogin() {
        courier.setLogin("courier");
        loginCourier()
                .then()
                .statusCode(404)
                .and()
                .assertThat()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Проверяем, что авторизация неуспешна при передаче неклрректного пароля")
    public void checkStatusCodeLoginWithIncorrectPassword() {
        courier.setPassword("null");
        loginCourier()
                .then()
                .statusCode(404)
                .and()
                .assertThat()
                .body("message", equalTo("Учетная запись не найдена"));
    }



    @After
    public void deleteCourier() {
        courierID = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post(courierLoginLink)
                .body().as(CourierID.class);
        given()
                .header("Content-type", "application/json")
                .body(courierID)
                .delete(courierMainLink + courierID.getId());
    }

}
