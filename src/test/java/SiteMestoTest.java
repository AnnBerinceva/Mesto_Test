import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class SiteMestoTest {

    String bearerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NjM2MzYyMmYwYTExODAwM2QyZDExMjAiLCJpYXQiOjE3MjMzODAzMzUsImV4cCI6MTcyMzk4NTEzNX0.bGfaqU_cYW_frQ_pp1l5GSP-mtiWf3Oe7QiupLBzJL4";

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-mesto.praktikum-services.ru";
    }

    @Test
    @DisplayName("Check status code of /users/me") // имя теста
    @Description("Описание теста №1: Basic test for /users/me endpoint") // описание теста
    public void getMyInfoStatusCode() {
        given()
                .auth().oauth2(bearerToken)
                .get("/api/users/me")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Add a new photo")
    @Description("This test is for adding a new photo to Mesto.")
    public void addNewPhoto() {
        given()
                .header("Content-type", "application/json") // Передаём Content-type в заголовке для указания типа файла
                .auth().oauth2(bearerToken) // Передаём токен для аутентификации
                .body("{\"name\":\"Москва\",\"link\":\"https://code.s3.yandex.net/qa-automation-engineer/java/files/paid-track/sprint1/photoSelenium.jpg\"}") // Формируем тело запроса
                .post("/api/cards") // Делаем POST-запрос
                .then().statusCode(201); // Проверяем код ответа
    }

    @Test
    @DisplayName("Like the first photo")
    @Description("This test is for liking the first photo on Mesto.")
    public void likeTheFirstPhoto() {
        String photoId = getTheFirstPhotoId();

        likePhotoById(photoId);
        deleteLikePhotoById(photoId);
    }

    @Step("Take the first photo from the list")
    private String getTheFirstPhotoId() {
        // Получение списка фотографий и выбор первой из него
        return given()
                .auth().oauth2(bearerToken) // Передаём токен для аутентификации
                .get("/api/cards") // Делаем GET-запрос
                .then().extract().body().path("data[0]._id"); // Получаем ID фотографии из массива данных
    }

    @Step("Like a photo by id")
    private void likePhotoById(String photoId) {
        // Лайк фотографии по photoId
        given()
                .auth().oauth2(bearerToken) // Передаём токен для аутентификации
                .put("/api/cards/{photoId}/likes", photoId) // Делаем PUT-запрос
                .then().assertThat().statusCode(200); // Проверяем, что сервер вернул код 200
    }

    @Step("Delete like from the photo by id")
    private void deleteLikePhotoById(String photoId) {
        // Снять лайк с фотографии по photoId
        given()
                .auth().oauth2(bearerToken) // Передаём токен для аутентификации
                .delete("/api/cards/{photoId}/likes", photoId) // Делаем DELETE-запрос
                .then().assertThat().statusCode(200); // Проверяем, что сервер вернул код 200
    }

    @Test
    @DisplayName("Check user name")
    @Description("This test is for check current user's name.")
    public void checkUserName() {
        given()
                .auth().oauth2(bearerToken) // Передаём токен для аутентификации
                .get("/api/users/me") // Делаем GET-запрос
                .then().assertThat().body("data.name", equalTo("Василий Васильев")); // Проверяем, что имя соответствует ожидаемому
    }

    @Test
    @DisplayName("Check user name and print response body") // имя теста
    @Description("Описание теста №3: This is a more complicated test with console output")
    @TmsLink("TestCase-112") // ссылка на тест-кейс
    @Issue("BUG-985") // ссылка на баг-репорт
    public void checkUserNameAndPrintResponseBody() {

        Response response =sendGetRequestUsersMe();
        // отправили запрос и сохранили ответ в переменную response - экземпляр класса Response

        compareUserNameToText(response, "Василий Васильев");
        // проверили, что в теле ответа ключу name соответствует нужное имя пользователя
        printResponseBodyToConsole(response); // вывели тело ответа на экран
    }
    // метод для шага "Отправить запрос":
    @Step("Send GET request to /api/users/me")
    public Response sendGetRequestUsersMe(){
        return given().auth().oauth2(bearerToken).get("/api/users/me");
    }

    // метод для шага "Сравнить имя пользователя с заданным":
    @Step("Compare user name to something")
    public void compareUserNameToText(Response response, String username){
        response.then().assertThat().body("data.name",equalTo(username));
    }

    // метод для шага "Вывести тело ответа в консоль":
    @Step("Print response body to console")
    public void printResponseBodyToConsole(Response response){
        System.out.println(response.body().asString());
    }
}