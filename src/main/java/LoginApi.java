import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class LoginApi {
    public static Response response;

    public static Response loginUser(String email, String password){
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
