import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AuthIntegrationTest {

  @BeforeAll
  static void setUp(){
    RestAssured.baseURI = "http://localhost:4004";
  }

  @Test
  public void shouldReturnOKWithValidToken() {
    String loginPayload = """
          {
            "email": "testuser@test.com",
            "password": "password123"
          }
        """;

   String token = given()
        .contentType("application/json")
        .body(loginPayload)
        .post("/auth/login")
        .then()
        .statusCode(200)
        .body("token", notNullValue())
        .extract()
        .response()
        .jsonPath()
        .getString("token");

    System.out.println("TEST: Generated Token: " + token);

    given()
        .header("Authorization", "Bearer " + token)
        .get("/auth/validate")
        .then()
        .log()
        .ifValidationFails()
        .statusCode(200);

  }

  @Test
  public void shouldReturnUnauthorizedOnInvalidLogin() {
    String loginPayload = """
          {
            "email": "invalid_user@test.com",
            "password": "wrongpassword"
          }
        """;

    given()
        .contentType("application/json")
        .body(loginPayload)
        .post("/auth/login")
        .then()
        .statusCode(401);
  }
}
