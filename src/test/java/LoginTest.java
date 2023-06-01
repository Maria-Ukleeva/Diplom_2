import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

public class LoginTest {
    public String email;
    public String password;
    public User user;

    @Before
    public void setUp(){
        user = RegisterApi.createUser();
        email = user.getEmail();
        password = user.getPassword();
        RegisterApi.registerUser(user);
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void shouldSuccessfullyLoginWithCorrectCredentials(){
        Response response = LoginApi.loginUser(email,password);
        response.then().assertThat().body("success", equalTo(true)).and().body("accessToken", containsString("Bearer"))
        .and().body("refreshToken", notNullValue()).and().body("user.email", equalTo(user.getEmail())).and().body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    public void shouldReturnErrorIfPasswordIncorrect(){
        Response response = LoginApi.loginUser(email,password+"1");
        response.then().statusCode(401).and().assertThat().body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с неверным логином")
    public void shouldReturnErrorIfUsernameIncorrect(){
        Response response = LoginApi.loginUser("1"+email,password);
        response.then().statusCode(401).and().assertThat().body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void cleanUp() {
        RegisterApi.deleteUser();
        }
}
