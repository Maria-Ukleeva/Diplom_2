import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserApi {

    public static Response changeUserData(User user, String token) {
        return given()
                .baseUri(Endpoints.BASEURL)
                .header("Content-type", "application/json")
                .auth().oauth2(token)
                .and()
                .body(user)
                .when()
                .patch(Endpoints.USER);
    }

    public static Response changeUserDataWithoutAuthorization(User user) {
        return given()
                .baseUri(Endpoints.BASEURL)
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .patch(Endpoints.USER);
    }

    public static void deleteUser(String accessToken) {
        try {
            given()
                    .baseUri(Endpoints.BASEURL)
                    .header("Content-type", "application/json")
                    .auth()
                    .oauth2(accessToken)
                    .delete(Endpoints.REGISTER);
        } catch (NullPointerException ignored) {
        }
    }
}
