import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrdersApi {
    public static Response getSpecificUserOrdersWithAuthorization(String token){
        return given()
                .baseUri(Endpoints.BASEURL)
                .header("Content-type", "application/json")
                .auth()
                .oauth2(token)
                .get(Endpoints.ORDERS);
    }

    public static Response getSpecificUserOrdersWithoutAuthorization(){
        return given()
                .baseUri(Endpoints.BASEURL)
                .header("Content-type", "application/json")
                .get(Endpoints.ORDERS);
    }
    public static Response createOrderWithAuthorization(List <String> ingredientId, String token){
        Order order = new Order(ingredientId);
        return given()
                .baseUri(Endpoints.BASEURL)
                .header("Content-type", "application/json")
                .auth()
                .oauth2(token)
                .and()
                .body(order)
                .post(Endpoints.ORDERS);
    }

    public static Response createOrderWithoutAuthorization(List <String> ingredientId){
        Order order = new Order(ingredientId);
        return given()
                .baseUri(Endpoints.BASEURL)
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .post(Endpoints.ORDERS);
    }

    public static Response createOrderWithoutIngredients(String token){
        Order order = new Order();
        return given()
                .baseUri(Endpoints.BASEURL)
                .header("Content-type", "application/json")
                .auth()
                .oauth2(token)
                .and()
                .body(order)
                .post(Endpoints.ORDERS);
    }

    public static List<String> getIngredient(){
        Response response = given()
                .baseUri(Endpoints.BASEURL)
                .header("Content-type", "application/json")
                .get(Endpoints.INGREDIENTS);
        return response.then().extract().path("data._id");
    }
}
