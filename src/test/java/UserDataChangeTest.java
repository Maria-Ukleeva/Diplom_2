import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserDataChangeTest {

    public String username;
    public String email;
    public String token;

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
    public void shouldSuccessfullyChangeDataWhenAuthorized(){
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
                .body("{\n" +
                        "\"email\": \"1"+email+"\",\n" +
                        "\"password\": \"password\",\n" +
                        "\"name\": \""+username+"\",\n" +
                                "\"token\": \""+token+"\"\n"+
                        "}")
                .when()
                .patch("api/auth/user");

        response1.then().body("success", equalTo(true)).and().body("user.name", equalTo("name"));
    }

    @Test
    public void shouldReturnErrorChangeDataWhenUnauthorized(){

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body("{\n" +
                        "\"email\": \""+email+"\",\n" +
                        "\"password\": \"password\",\n" +
                        "\"name\": \"name\",\n" +
                        "\"token\": \"\"\n"+
                        "}")
                .when()
                .patch("api/auth/user");

        response.then().statusCode(401).body("success", equalTo(false)).and().body("message", equalTo("You should be authorised"));
    }

    @Test
    public void shouldReturnErrorChangeDataWhenEmailIsUsed(){
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
                .body("{\n" +
                        "\"email\": \""+email+"\",\n" +
                        "\"password\": \"password\",\n" +
                        "\"name\": \"name\",\n" +
                        "\"token\": \""+token+"\"\n"+
                        "}")
                .when()
                .patch("api/auth/user");

        response1.then().statusCode(403).body("success", equalTo(false)).and().body("message", equalTo("User with such email already exists"));
    }
}
