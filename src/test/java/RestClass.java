import io.cucumber.gherkin.internal.com.eclipsesource.json.JsonObject;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import io.cucumber.java.ParameterType;
import io.cucumber.java.ru.Тогда;
import io.cucumber.java.ru.И;
import io.qameta.allure.Allure;
import org.testng.Assert;
import static io.restassured.RestAssured.given;

public class RestClass {

    public static RequestSpecification requestSpec = given()
            .contentType(ContentType.JSON)
            .baseUri("https://demoqa.com")
            .log().all()
            .filter(new AllureRestAssured());
    public static RequestSpecification requestSpecWithBody = given()
            .contentType(ContentType.JSON)
            .baseUri("https://demoqa.com")
            .log().body()
            .filter(new AllureRestAssured());

    @ParameterType(".*")
    public String StringFromFeatures(String value) {
        return value;
    }
    @ParameterType("true|false")
    public boolean booleanFromFeatures(String value) {
        return Boolean.parseBoolean(value);
    }
    @ParameterType("-?\\d+(\\.\\d+)?")
    public double doubleFromFeatures(String value) {
        return Double.parseDouble(value);
    }

    ThreadLocal<JsonObject> jsonBodyThreadLocal = new ThreadLocal<>();
    ThreadLocal<String> authToken = new ThreadLocal();
    ThreadLocal<String> userId = new ThreadLocal();
    ThreadLocal<List> bookNamesFromCatalog = new ThreadLocal();
    ThreadLocal<List> isbnBookNamesFromCatalog = new ThreadLocal();
    ThreadLocal<Response> responseJsonThreadLocal = new ThreadLocal<>();

    @Тогда("Запрашиваем userId для выбранной УЗ")
    public void getUserIdStep() {
        userId.set(getUserId(jsonBodyThreadLocal.get()));
        System.out.println("ID авторизованного пользователя: " + userId.get());
        Allure.step(String.format("%s: ожидаем %s \n фактически %s", "userId", userId.get(), userId.get()));
    }
    @Тогда("Запрашиваем данные пользователя")
    public void getUserInfoStep() {
        responseJsonThreadLocal.set(getUserInfo(userId.get(), authToken.get()));
    }
    @Тогда("Запрашиваем все наименования книг в каталоге")
    public void getBooksNamesFromCatalogStep() {
        bookNamesFromCatalog.set(getNameBooksFromCatalog());
        System.out.println("Наименования книг в каталоге: " + bookNamesFromCatalog.get());
        Allure.step(String.format("%s: ожидаем %s \n фактически %s", "Книги", bookNamesFromCatalog.get(), bookNamesFromCatalog.get()));
    }
    @Тогда("Запрашиваем данные всех книг в каталоге")
    public void getDataAboutBooksFromCatalogStep() {
        responseJsonThreadLocal.set(getDataAboutBooksFromCatalog());
    }
    @Тогда("Запрашиваем все ID ISBN книг в каталоге")
    public void getIsbnNamesFromCatalogStep() {
        isbnBookNamesFromCatalog.set(getIsbnBooksFromCatalog());
        System.out.println("ID книг в каталоге: " + isbnBookNamesFromCatalog.get());
        Allure.step(String.format("%s: ожидаем %s \n фактически %s", "ISBN", isbnBookNamesFromCatalog.get(), isbnBookNamesFromCatalog.get()));
    }
    @Тогда("Подготавливаем запрос с телом")
    public void setRequestWithBodyStep() throws IOException {
        jsonBodyThreadLocal.set(JsonObject.readFrom(Files.readString(Paths.get("src/test/resources/JSON/requestBody.json"))));
    }
    @И("Устанавливает значение {StringFromFeatures} в {StringFromFeatures}")
    public void setAttributesInJsonBodyStep(String field, String valueOfField) {
        JsonObject jsonElement = jsonBodyThreadLocal.get();
        jsonElement.set(field, valueOfField);
    }
    @Тогда("Отправляем подготовленный запрос для получения токена")
    public void sendRequestToGetTokenStep() {
        authToken.set(getToken(jsonBodyThreadLocal.get()));
    }
    @Тогда("Отправляем подготовленный запрос для получения userID")
    public void sendRequestToGetUserIdStep() {
        userId.set(getUserId(jsonBodyThreadLocal.get()));
    }
    @Тогда("Отправляем подготовленный запрос для получения данных книги с индексом {int}")
    public void sendRequestStep(int index) {
        responseJsonThreadLocal.set(getDataAboutBookByIndex(isbnBookNamesFromCatalog.get().get(index).toString()));
    }
    @Тогда("Проверяем, что в ответе строковое поле {StringFromFeatures} равно {StringFromFeatures}")
    public void assertStringStep(String field, String expectedValue) {
        String result = responseJsonThreadLocal.get().jsonPath().getString(field);
        Assert.assertEquals(result, expectedValue, "Результат не соответствует ожидаемому");
        Allure.step(String.format("%s: ожидаем %s \n фактически %s", field, result, expectedValue));
    }
    @Тогда("Проверяем, что в ответе числовое поле {StringFromFeatures} равно {int}")
    public void assertIntStep(String field, int expectedValue) {
        int result = responseJsonThreadLocal.get().jsonPath().getInt(field);
        Assert.assertEquals(result, expectedValue, "Результат не соответствует ожидаемому");
        Allure.step(String.format("%s: ожидаем %s \n фактически %s", field, result, expectedValue));
    }

    public static void main(String[] args) {
        // Место для написания новых степов и их изолированного тестирования
    }

    public static String getToken(JsonObject jsonBody) {
        return requestSpecWithBody
                .body(jsonBody.toString())
                .post("/Account/v1/GenerateToken")
                .jsonPath()
                .getString("token");
    }
    public static String getUserId(JsonObject jsonBody) {
        return requestSpecWithBody
                .body(jsonBody.toString())
                .post( "/Account/v1/Login")
                .jsonPath()
                .getString("userId");
    }
    public static Response getUserInfo(String userId, String token) {
        return requestSpec
                .header("Authorization", "Bearer " + token)
                .get("/Account/v1/User/" + userId);
    }
    public static List<String> getNameBooksFromCatalog() {
        return requestSpec
                .get("/BookStore/v1/Books")
                .jsonPath()
                .getList("books.title");
    }
    public static Response getDataAboutBooksFromCatalog() {
        return requestSpec.get("/BookStore/v1/Books");
    }
    public static List<String> getIsbnBooksFromCatalog() {
        return requestSpec
                .get("/BookStore/v1/Books")
                .jsonPath()
                .getList("books.isbn");
    }
    public static Response getDataAboutBookByIndex(String isbn) {
        RequestSpecification localSpec = given()
                .baseUri("https://demoqa.com")
                .log().all()
                .filter(new AllureRestAssured());
        return localSpec.param("ISBN", isbn).get("/BookStore/v1/Book");
    }
}