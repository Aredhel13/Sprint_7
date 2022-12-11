import Models.*;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {
    Courier courier;
    CourierID courierID;
    String courierLoginLink = "/api/v1/courier/login";
    String courierMainLink = "/api/v1/courier";

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        courier = new Courier("courier132", "qwerty", "Halk");
    }

    @Step("Создаём курьера")
    public Response createCourier() {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post(courierMainLink);
    }

    @Test
    @DisplayName("Проверяем код и тело ответа при успешном создании курьера")
    public void checkStatusCodeSuccessCreate() {
        createCourier()
                .then()
                .statusCode(201)
                .and()
                .assertThat()
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Проверяем, что нельзя создать двух одинаковых курьеров")
    public void checkStatusCodeWhenDuplicateCourierCreate() {
        createCourier();
        createCourier()
                .then()
                .statusCode(409)
                .and()
                .assertThat()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Проверяем, что необходимо заполнять обязательное поле: логин")
    public void checkStatusCodeCreateWithoutLogin() {
        courier.setLogin("");
        createCourier()
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Проверяем, что необходимо заполнять обязательное поле: логин")
    public void checkStatusCodeCreateWithNullLogin() {
        courier.setLogin(null);
        createCourier()
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Проверяем, что необходимо заполнять обязательное поле: пароль")
    public void checkStatusCodeCreateWithNullPassword() {
        courier.setPassword(null);
        createCourier()
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Проверяем, что можно не заполнять поле: имя")
    public void checkStatusCodeCreateWithNullName() {
        courier.setFirstName(null);
        createCourier()
                .then()
                .statusCode(201)
                .and()
                .assertThat()
                .body("ok", equalTo(true));
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
