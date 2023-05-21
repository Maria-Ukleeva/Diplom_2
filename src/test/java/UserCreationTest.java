import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserCreationTest {
    public String username;
    public String email;

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        username = "mu" + new Random().nextInt(1000);
        email = username+"@ya.ru";
    }

    @Test
    public void shouldCreateNewUniqueUser(){
        User user = new User(email, "password", username);
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("api/auth/register");

        response.then().assertThat().body("success", equalTo(true)).and().body("email", equalTo(email))
                .and().body("name", equalTo(username)).and().body("accessToken", notNullValue()).and().body("resfreshToken", notNullValue());
    }

    @Test
    public void shouldReturnErrorIfUserExists(){
        User user = new User(email, "password", username);
        given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("api/auth/register");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("api/auth/register");

        response.then().statusCode(403).and().assertThat().body("success", equalTo(false)).and().body("message", equalTo("User already exists"));
    }

    @Test
    public void shouldReturnErrorIfFieldIsMissing(){
        User user = new User("password", username);
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("api/auth/register");

        response.then().statusCode(403).and().assertThat().body("success", equalTo(false)).and().body("message", equalTo("Email, password and name are required fields"));
    }
}
