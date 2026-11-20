package com.mhwlng.taskservice;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.mhwlng.taskservice.controller.TaskController;
import com.mhwlng.taskservice.service.TaskService;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;


@ActiveProfiles("testdb")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TaskServiceApplicationTests {

    @Autowired
    private TaskController taskController;

    @Autowired
    private TaskService taskService;

    @Test
    public void contextLoads() throws Exception {
        assertThat(taskController).isNotNull();
        assertThat(taskService).isNotNull();
    }

  @BeforeAll
  static void setUp(){
    RestAssured.baseURI = "http://localhost:4000";
  }

  @Test
  public void executeCrudFunctions () {

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

    //POST /api/v1/tasks

    String id = given()
        .contentType("application/json")
        .body(payload)
        .when()
        .post("/api/v1/tasks")
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
        .when()
        .get("/api/v1/tasks/" + id)
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
        .contentType("application/json")
        .body(payload2)
        .when()
        .put("/api/v1/tasks/" + id)
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
        .when()
        .get("/api/v1/tasks")
        .then()
        .statusCode(200)
        .body("tasks", notNullValue())
        .extract()
        .jsonPath()
        .getString("tasks[0].title");

    assertThat(title3).isEqualTo("testtitle2");

    //SEARCH /api/v1/tasks/search

    String title4 = given()
        .when()
        .queryParam("title","testtitle2")
        .get("/api/v1/tasks/search")
        .then()
        .statusCode(200)
        .body("tasks", notNullValue())
        .extract()
        .jsonPath()
        .getString("tasks[0].title");

    assertThat(title4).isEqualTo("testtitle2");

    //DELETE /api/v1/tasks/$ID

    given()
        .when()
        .delete("/api/v1/tasks/" + id)
        .then()
        .statusCode(200);

  }

}
