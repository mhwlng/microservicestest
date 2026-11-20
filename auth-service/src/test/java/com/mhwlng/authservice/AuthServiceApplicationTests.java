package com.mhwlng.authservice;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.mhwlng.authservice.controller.AuthController;
import com.mhwlng.authservice.service.AuthService;
import com.mhwlng.authservice.service.UserService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;


import io.restassured.RestAssured;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("testdb")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AuthServiceApplicationTests {

  @Autowired
  private AuthController authController;

  @Autowired
  private AuthService authService;

  @Autowired
  private UserService userService;

  @BeforeAll
  static void setUp(){
    RestAssured.baseURI = "http://localhost:4005";
  }

  @Test
  public void contextLoads() throws Exception {
      assertThat(authController).isNotNull();
      assertThat(authService).isNotNull();
      assertThat(userService).isNotNull();
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
        .post("/login")
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
        .get("/validate")
        .then()
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
        .post("/login")
        .then()
        .statusCode(401);
  }




}


