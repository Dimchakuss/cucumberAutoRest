import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import io.cucumber.java.ParameterType;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import io.qameta.allure.Allure;
import org.testng.Assert;

import static io.restassured.RestAssured.given;

public class RestClass {

    private static RequestSpecification requestSpec = given()
            .contentType(ContentType.JSON)
            .baseUri("https://demoqa.com")
            .log().all()
            .filter(new ResponseLoggingFilter());
    private static RequestSpecification requestSpecWithBody = given()
            .contentType(ContentType.JSON)
            .baseUri("https://demoqa.com")
            .log().body()
            .filter(new ResponseLoggingFilter());

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

    public static String userName = "user";
    public static String password = "Pa$$w0rd";
    public static String tokenFromStep;
    public static String userIdFromStep;
    public static List<String> isbnBookNamesFromCatalogFromStep;


    @Когда("Пользователь вводит логин {StringFromFeatures}, пароль {StringFromFeatures} получает авторизационный токен")
    public void getTokenStep(String userName, String password) {
        String token = getToken(userName, password);
        System.out.println("Полученный токен: " + token);
        Allure.step(String.format("%s: ожидаем %s \n фактически %s", "Токены", token, token));
    }
    @Тогда("Запрашиваем userId для УЗ логин {StringFromFeatures}, пароль {StringFromFeatures}")
    public void getUserIdStep(String userName, String password) {
        String userId = getUserId(userName, password);
        System.out.println("ID авторизованного пользователя: " + userId);
        userIdFromStep = userId;
        Allure.step(String.format("%s: ожидаем %s \n фактически %s", "userId", userIdFromStep, userIdFromStep));
    }
    @Тогда("Запрашиваем данные пользователя {StringFromFeatures}")
    public void getUserInfoStep(String userTypeinfo) {
        String nameAuthorizedUser = getUserInfo(userIdFromStep, tokenFromStep, userTypeinfo);
        System.out.println("Авторизованный пользователь: " + nameAuthorizedUser);
        Allure.step(String.format("%s: ожидаем %s \n фактически %s", userTypeinfo, nameAuthorizedUser, nameAuthorizedUser));
    }
    @Тогда("Запрашиваем все наименования книг в каталоге")
    public void getBooksNamesFromCatalogStep() {
        List<String> titleBooksNamesFromCatalog = getBooksNamesFromCatalog();
        System.out.println("Наименования книг в каталоге: " + titleBooksNamesFromCatalog);
        Allure.step(String.format("%s: ожидаем %s \n фактически %s", "Книги", titleBooksNamesFromCatalog, titleBooksNamesFromCatalog));
    }
    @Тогда("Запрашиваем все ID ISBN книг в каталоге")
    public void getIsbnNamesFromCatalogStep() {
        List<String> isbnBookNamesFromCatalog = getIsbnNamesFromCatalog();
        System.out.println("ID книг в каталоге: " + isbnBookNamesFromCatalog);
        isbnBookNamesFromCatalogFromStep = isbnBookNamesFromCatalog;
        Allure.step(String.format("%s: ожидаем %s \n фактически %s", "ISBN", isbnBookNamesFromCatalogFromStep, isbnBookNamesFromCatalogFromStep));
    }
    @Тогда("Запрашиваем автора книги с индексом {int}")
    public void getAuthorByIsbnStep(int index) {
        String authorsByIsbn = getAuthorByIsbn(isbnBookNamesFromCatalogFromStep.get(index));
        System.out.println("Автор книги с ID (" + isbnBookNamesFromCatalogFromStep.get(index) + "): " + authorsByIsbn);
        Allure.step(String.format("%s: ожидаем %s \n фактически %s", "Автор книги", authorsByIsbn, authorsByIsbn));
    }

    public static void main(String[] args) {

        // Блок кода для написания новых степов и их изолированного тестирования

        List<String> isbnBookNamesFromCatalog = getIsbnNamesFromCatalog();
        System.out.println("ID книг в каталоге: " + isbnBookNamesFromCatalog);
        isbnBookNamesFromCatalogFromStep = isbnBookNamesFromCatalog;

        String authorsByIsbn = getAuthorByIsbn(isbnBookNamesFromCatalogFromStep.get(1));
        System.out.println("Автор книги с ID (" + isbnBookNamesFromCatalogFromStep.get(1) + "): " + authorsByIsbn);

    }
    public static String getToken(String userName, String password) {
        Response response = requestSpecWithBody
                .body("{\"userName\": \"" + userName + "\", \"password\": \"" + password + "\"}")
                .post("/Account/v1/GenerateToken");
        return response.jsonPath().getString("token");
    }
    public static String getUserId(String userName, String password) {
        Response response = requestSpecWithBody
                .body("{\"userName\": \"" + userName + "\", \"password\": \"" + password + "\"}")
                .post( "/Account/v1/Login");
        return response.jsonPath().getString("userId");
    }
    public static String getUserInfo(String userId, String token, String typeInfo) {
        requestSpec.header("Authorization", "Bearer " + token);
        Response response = requestSpec.get("/Account/v1/User/" + userId);
        return response.jsonPath().getString(""+ typeInfo +"");
    }
    public static List<String> getBooksNamesFromCatalog() {
        Response response = requestSpec.get("/BookStore/v1/Books");
        return response.jsonPath().getList("books.title");
    }
    public static List<String> getIsbnNamesFromCatalog() {
        Response response = requestSpec.get("/BookStore/v1/Books");
        return response.jsonPath().getList("books.isbn");
    }
    public static String getAuthorByIsbn(String isbn) {
        RequestSpecification localSpec = given()
                .baseUri("https://demoqa.com")
                .log().all()
                .filter(new ResponseLoggingFilter());

        Response response = localSpec
                .param("ISBN", isbn)
                .get("/BookStore/v1/Book");

        return response.jsonPath().getString("author");
    }
}