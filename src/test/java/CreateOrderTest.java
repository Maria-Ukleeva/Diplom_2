import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }
    @Test
    public void shouldCreateSuccessfulOderWithValidIngredients(){
        Response response = given()
                .header("Content-type", "application/json")
                .get("api/ingredients");
        List<String> ingredientIds = response.then().extract().path("data._id");
        String ingredientId = ingredientIds.get(0);

        Response response1 = given()
                .header("Content-type", "application/json")
                .and()
                .body("{ \"ingredients\" : \""+ingredientId+"\"}")
                .post("api/orders");
        response1.then().body("success", equalTo(true)).and().body("order.number", notNullValue());
    }

    @Test
    public void shouldReturnErrorForInvalidIngredients(){
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body("{ \"ingredients\" : \"111\"}")
                .post("api/orders");
        response.then().statusCode(500);
    }

    @Test
    public void shouldReturnErrorForNoIngredients(){
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body("{ \"ingredients\" : \"\"}")
                .post("api/orders");
        response.then().statusCode(400).and().body("success", equalTo(false)).and().body("message", equalTo("Ingredient ids must be provided"));
    }

}
