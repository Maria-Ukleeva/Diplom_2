import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;

public class UserDataChangeTest {

    public String token;
    public User user;

    @Before
    public void setUp() {
        user = RegisterApi.createUser();
        RegisterApi.registerUser(user);
    }
    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    public void shouldSuccessfullyChangeDataWhenAuthorized() {
        LoginApi.loginUser(user);
        token = LoginApi.getAccessToken();
        User newUser = RegisterApi.createUser();
        Response response = UserApi.changeUserData(newUser, token);
        response.then().body("success", equalTo(true)).and().body("user.name", equalTo(RegisterApi.getUsername())).and().body("user.email", equalTo(RegisterApi.getEmail()));
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void shouldReturnErrorChangeDataWhenUnauthorized() {
        User newUser = RegisterApi.createUser();
        LoginApi.loginUser(user);
        token = LoginApi.getAccessToken();
        Response response = UserApi.changeUserDataWithoutAuthorization(newUser);
        response.then().statusCode(401).body("success", equalTo(false)).and().body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение данных пользователя на уже занятый email с авторизацией")
    public void shouldReturnErrorChangeDataWhenEmailIsUsed() {
        LoginApi.loginUser(user);
        User secondUser = RegisterApi.createUser();
        RegisterApi.registerUser(secondUser);
        token = LoginApi.getAccessToken();
        String secondEmail = RegisterApi.getEmail();
        User newUser = new User(secondEmail, "password", "name");
        Response response = UserApi.changeUserData(newUser, token);
        response.then().statusCode(403).body("success", equalTo(false)).and().body("message", equalTo("User with such email already exists"));
    }

    @After
    public void cleanUp() {
        RegisterApi.deleteUser();
        }
    }
