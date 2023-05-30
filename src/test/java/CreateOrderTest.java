import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest {
    public User user;
    public String token;
    @Before
    public void setUp() {
        user = RegisterApi.createUser();
        RegisterApi.registerUser(user);
        LoginApi.loginUser(user);
        token = LoginApi.getAccessToken();
    }

    @Test
    @DisplayName("Создание заказа с ингридиентами с авторизацией")
    public void shouldCreateSuccessfulOderWithValidIngredients(){
        List<String> ingredientIds = OrdersApi.getIngredient();
        Response response = OrdersApi.createOrderWithAuthorization(ingredientIds, token);
        response.then().body("success", equalTo(true)).and().body("order.number", notNullValue());
    }
    @Test
    @DisplayName("Создание заказа с ингридиентами без авторизации")
    public void shouldCreateSuccessfulOderWithValidIngredientsUnauthorized(){
        List<String> ingredientIds = OrdersApi.getIngredient();
        Response response = OrdersApi.createOrderWithoutAuthorization(ingredientIds);
        response.then().body("success", equalTo(true)).and().body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингридиентов")
    public void shouldReturnErrorForInvalidIngredients(){
        List<String> ingredientIds = Arrays.asList("1111fdf", "00sdjskh");
        Response response = OrdersApi.createOrderWithAuthorization(ingredientIds, token);
        response.then().statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа без ингридиентов")
    public void shouldReturnErrorForNoIngredients(){
        Response response = OrdersApi.createOrderWithoutIngredients(token);
        response.then().statusCode(400).and().body("success", equalTo(false)).and().body("message", equalTo("Ingredient ids must be provided"));
    }
    @After
    public void cleanUp() {
            RegisterApi.deleteUser();
        }

}
