import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PraktikumTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-mesto.praktikum-services.ru";
    }

    @Test
    public void registrationAndAuth() {
        //Составление email
        Random random = new Random();
        String email = "something" + random.nextInt(10000000) + "@yandex.ru";

        //Составление json, используя переменную email
        String json = "{\"email\": \"" + email + "\", \"password\": \"aaa\" }";

        //POST запрос на регистрацию signup
        given()
                .header("Content-type", "application/json")
                .body(json)
                .post("/api/signup")
                .then()
                .statusCode(201);

        //POST запрос на авторизацию signin с теми же параметрами
        Response response = given()
                .header("Content-type", "application/json")
                .body(json)
                .post("/api/signin");
        response.then().assertThat()
                .body("token", notNullValue())
                .and()
                .statusCode(200);

        // Попытка зарегистрироваться с теми же параметрами
        given()
                .header("Content-type", "application/json")
                .body(json)
                .post("/api/signup")
                .then()
                // проверь, что статус ответа изменился
                .statusCode(409);
    }
}