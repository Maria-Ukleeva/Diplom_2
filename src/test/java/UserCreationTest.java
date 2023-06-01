import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserCreationTest {

    @Test
    @DisplayName("Создание нового уникального пользователя")
    public void shouldCreateNewUniqueUser(){
        User user = RegisterApi.createUser();
        Response response = RegisterApi.registerUser(user);
        response.then().assertThat().body("success", equalTo(true)).and().body("user.email", equalTo(RegisterApi.email))
                .and().body("user.name", equalTo(RegisterApi.username)).and().body("accessToken", notNullValue()).and().body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void shouldReturnErrorIfUserExists(){
        User firstUser = RegisterApi.createUser();
        RegisterApi.registerUser(firstUser);
        Response response = RegisterApi.registerUser(firstUser);
        response.then().statusCode(403).and().assertThat().body("success", equalTo(false)).and().body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без одного из обязательных полей")
    public void shouldReturnErrorIfFieldIsMissing(){
        User user = RegisterApi.createUserWithoutAField();
        Response response = RegisterApi.registerUser(user);
        response.then().statusCode(403).and().assertThat().body("success", equalTo(false)).and().body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void cleanUp() {
       RegisterApi.deleteUser();
    }
}
