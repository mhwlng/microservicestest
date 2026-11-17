import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TasksIntegrationTest {
  @BeforeAll
  static void setUp(){
    RestAssured.baseURI = "http://localhost:4004";
  }

  @Test
  public void shouldReturnTasksWithValidToken () {
    String loginPayload = """
          {
            "email": "testuser@test.com",
            "password": "password123"
          }
        """;

    String token = given()
        .contentType("application/json")
        .body(loginPayload)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(200)
        .extract()
        .jsonPath()
        .get("token");

    System.out.println("Generated Token: " + token);

    List<String> tasks = given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/api/tasks")
        .then()
        .statusCode(200)
        .body("tasks", notNullValue())
        .extract()
        .jsonPath()
        .getList("tasks.title");

    System.out.println("Tasks: " + tasks.toString());


  }
}
