import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersTest {
    public String username;
    public String email;
    public String token;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        username = "mu" + new Random().nextInt(1000);
        email = username + "@ya.ru";
        User user = new User(email, "password", username);
        given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("api/auth/register");
    }

    @Test
    public void shouldGetUserOrdersWhenAuthorized() {
        Credentials credentials = new Credentials(email, "password");
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(credentials)
                .when()
                .post("api/auth/login");

        token = response.body().as(Session.class).getAccessToken().substring(7);

        Response response1 = given()
                .header("Content-type", "application/json")
                .auth()
                .oauth2(token)
                .get("api/orders");

        response1.then().body("success", equalTo(true)).and().body("orders", notNullValue());
    }

    @Test
    public void shouldReturnErrorWhenUnauthorized() {

        Response response1 = given()
                .header("Content-type", "application/json")
                .get("api/orders");

        response1.then().statusCode(401).body("success", equalTo(false)).and().body("message", equalTo( "You should be authorised"));
    }

    @Test
    public void shouldGetAllOrders() {
        Response response1 = given()
                .header("Content-type", "application/json")
                .get("api/orders/all");

        response1.then().body("success", equalTo(true)).and().body("orders", notNullValue());
    }

}
