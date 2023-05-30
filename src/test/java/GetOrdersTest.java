import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersTest {
    public String token;
    public User user;

    @Before
    public void setUp() {
        user = RegisterApi.createUser();
        RegisterApi.registerUser(user);
        LoginApi.loginUser(user);
        token = LoginApi.getAccessToken();
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя с авторизацией")
    public void shouldGetUserOrdersWhenAuthorized() {
        Response response = OrdersApi.getSpecificUserOrdersWithAuthorization(token);
        response.then().body("success", equalTo(true)).and().body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя без авторизации")
    public void shouldReturnErrorWhenUnauthorized() {
        Response response = OrdersApi.getSpecificUserOrdersWithoutAuthorization();
        response.then().statusCode(401).body("success", equalTo(false)).and().body("message", equalTo( "You should be authorised"));
    }

    @After
    public void cleanUp() {
        RegisterApi.deleteUser();
    }

}
