import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class LoginTest {
    public String username;
    public String email;

    @Before
    public void setUp(){
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
    }

    @Test
    public void shouldSuccessfullyLoginWithCorrectCredentials(){
        Credentials credentials = new Credentials(email, "password");
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(credentials)
                .when()
                .post("api/auth/login");


        response.then().assertThat().body("success", equalTo(true)).and().body("accessToken", containsString("Bearer"))
        .and().body("refreshToken", notNullValue()).and().body("user.email", equalTo(email)).and().body("user.name", equalTo(username));
    }

    @Test
    public void shouldReturnErrorIfCredentialsIncorrect(){
        Credentials credentials = new Credentials(email, "psswrd");
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(credentials)
                .when()
                .post("api/auth/login");

        response.then().statusCode(401).and().assertThat().body("message", equalTo("email or password are incorrect"));
    }
    @Test
    public void shouldReturnErrorIfFieldIsMissing(){
        Credentials credentials = new Credentials(email);
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(credentials)
                .when()
                .post("api/auth/login");

        response.then().statusCode(401).and().assertThat().body("message", equalTo("email or password are incorrect"));
    }
}
