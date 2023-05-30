import com.github.javafaker.Faker;
import io.restassured.response.Response;
import lombok.Getter;
import static io.restassured.RestAssured.given;

public class RegisterApi {

    @Getter
    public static String username;
    @Getter
    public static String email;
    public static Response response;


    public static User createUser() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        email = faker.internet().emailAddress();
        return new User(email, Password.PASSWORD, username);
    }

    public static User createUserWithoutAField() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        return new User(Password.PASSWORD, username);
    }

    public static Response registerUser(User user){
        response = given()
                .baseUri(Endpoints.BASEURL)
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(Endpoints.REGISTER);
        return  response;
    }

    public static void deleteUser(){
        try{
            String accessToken = response.then().extract().path("accessToken").toString().substring(7);
            given()
                    .baseUri(Endpoints.BASEURL)
                    .header("Content-type", "application/json")
                    .auth()
                    .oauth2(accessToken)
                    .delete(Endpoints.REGISTER);
            System.out.println("Юзер удален");
        } catch (NullPointerException ignored){}
        }
}
