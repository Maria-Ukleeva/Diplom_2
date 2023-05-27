import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest {
    public String username;
    public String email;
    public String token;
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        username = "mu" + new Random().nextInt(1000);
        email = username+"@ya.ru";
        User user = new User(email, "password", username);
        given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("api/auth/register");

        Credentials credentials = new Credentials(email, "password");
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(credentials)
                .when()
                .post("api/auth/login");
        token = response.body().as(Session.class).getAccessToken().substring(7);
    }

    @Test
    @DisplayName("Создание заказа с ингридиентами с авторизацией")
    public void shouldCreateSuccessfulOderWithValidIngredients(){
        Response response = given()
                .header("Content-type", "application/json")
                .get("api/ingredients");
        List<String> ingredientIds = response.then().extract().path("data._id");
        String ingredientId = ingredientIds.get(0);

        Response response1 = given()
                .header("Content-type", "application/json")
                .auth()
                .oauth2(token)
                .and()
                .body("{ \"ingredients\" : \""+ingredientId+"\"}")
                .post("api/orders");
        response1.then().body("success", equalTo(true)).and().body("order.number", notNullValue());
    }
    @Test
    @DisplayName("Создание заказа с ингридиентами без авторизации")
    public void shouldCreateSuccessfulOderWithValidIngredientsUnauthorized(){
        Response response = given()
                .header("Content-type", "application/json")
                .get("api/ingredients");
        List<String> ingredientIds = response.then().extract().path("data._id");
        String ingredientId = ingredientIds.get(0);

        Response response1 = given()
                .header("Content-type", "application/json")
                .and()
                .body("{ \"ingredients\" : \""+ingredientId+"\"}")
                .post("api/orders");
        response1.then().body("success", equalTo(true)).and().body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингридиентов")
    public void shouldReturnErrorForInvalidIngredients(){
        Response response = given()
                .header("Content-type", "application/json")
                .auth()
                .oauth2(token)
                .and()
                .body("{ \"ingredients\" : \"111\"}")
                .post("api/orders");
        response.then().statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа без ингридиентов")
    public void shouldReturnErrorForNoIngredients(){
        Response response = given()
                .header("Content-type", "application/json")
                .auth()
                .oauth2(token)
                .and()
                .body("{ \"ingredients\" : \"\"}")
                .post("api/orders");
        response.then().statusCode(400).and().body("success", equalTo(false)).and().body("message", equalTo("Ingredient ids must be provided"));
    }

}
