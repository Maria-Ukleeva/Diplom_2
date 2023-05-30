import com.github.javafaker.Faker;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class LoginApi {
    private static String email;
    private static String password;
    public static Response response;

    public static Response loginUser(User user){
        email = user.getEmail();
        password = user.getPassword();
        Credentials credentials = new Credentials(email, password);
        response = given()
                .baseUri(Endpoints.BASEURL)
                .header("Content-type", "application/json")
                .and()
                .body(credentials)
                .when()
                .post(Endpoints.LOGIN);
        return response;
    }

    public static Response loginUserWithIncorrectEmail(User user){
        Faker faker = new Faker();
        email = faker.internet().emailAddress();
        password = user.getPassword();
        Credentials credentials = new Credentials(email, password);
        response = given()
                .baseUri(Endpoints.BASEURL)
                .header("Content-type", "application/json")
                .and()
                .body(credentials)
                .when()
                .post(Endpoints.LOGIN);
        return response;
    }

    public static Response loginUserWithIncorrectPassword(User user){
        Faker faker = new Faker();
        email = user.getEmail();
        password = faker.internet().password();
        Credentials credentials = new Credentials(email, password);
        response = given()
                .baseUri(Endpoints.BASEURL)
                .header("Content-type", "application/json")
                .and()
                .body(credentials)
                .when()
                .post(Endpoints.LOGIN);
        return response;
    }

    public static String getAccessToken(){
        return response.then().extract().path("accessToken").toString().substring(7);
    }

}
