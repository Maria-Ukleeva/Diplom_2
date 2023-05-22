import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

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
    public void shouldGetOrdersWhenAuthorized() {
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
                .and()
                .body("{\"token\": \"" + token + "\"}")
                .when()
                .get("api/auth/orders");

        response1.then().body("success", equalTo(true));
    }
}
