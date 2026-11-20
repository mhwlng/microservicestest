import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


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

    String payload = """
        {
            "title":"testtitle",
            "description": "testdescription",
            "completed":true
        }
        """;

    String payload2 = """
        {
            "title":"testtitle2",
            "description": "testdescription2",
            "completed":false
        }
        """;        

    String token = given()
        .contentType("application/json")
        .body(loginPayload)
        .post("/auth/login")
        .then()
        .statusCode(200)
        .extract()
        .jsonPath()
        .get("token");

    System.out.println("Generated Token: " + token);

    //POST /api/v1/tasks

    String id = given()
        .header("Authorization", "Bearer " + token)
        .contentType("application/json")
        .body(payload)
        .post("/api/tasks")
        .then()
        .statusCode(201)
        .body("id", notNullValue())
        .extract()
        .response()
        .jsonPath()
        .getString("id");

    System.out.println("TEST: id: " + id);

    //GET /api/v1/tasks/$ID

    String title = given()
        .header("Authorization", "Bearer " + token)
        .contentType("application/json")
        .get("/api/tasks/" + id)
        .then()
        .statusCode(200)
        .body("title", notNullValue())
        .extract()
        .response()
        .jsonPath()
        .getString("title");

    assertThat(title).isEqualTo("testtitle");

    //PUT /api/v1/tasks/$ID

    String title2 = given()
        .header("Authorization", "Bearer " + token)
        .contentType("application/json")
        .body(payload2)
        .put("/api/tasks/" + id)
        .then()
        .statusCode(200)
        .body("title", notNullValue())
        .extract()
        .response()
        .jsonPath()
        .getString("title");

    assertThat(title2).isEqualTo("testtitle2");

    //GET /api/v1/tasks
    String title3 = given()
        .header("Authorization", "Bearer " + token)
        .contentType("application/json")
        .get("/api/tasks")
        .then()
        .statusCode(200)
        .body("tasks", notNullValue())
        .extract()
        .jsonPath()
        .getString("tasks[0].title");

    assertThat(title3).isEqualTo("testtitle2");

    //SEARCH /api/v1/tasks/search

    String title4 = given()
        .header("Authorization", "Bearer " + token)
        .contentType("application/json")
        .queryParam("title","testtitle2")
        .get("/api/tasks/search")
        .then()
        .statusCode(200)
        .body("tasks", notNullValue())
        .extract()
        .jsonPath()
        .getString("tasks[0].title");

    assertThat(title4).isEqualTo("testtitle2");

    //DELETE /api/v1/tasks/$ID

    given()
        .header("Authorization", "Bearer " + token)
        .delete("/api/tasks/" + id)
        .then()
        .statusCode(200);

  }
}
