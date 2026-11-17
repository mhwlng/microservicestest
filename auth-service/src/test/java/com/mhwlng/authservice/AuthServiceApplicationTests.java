package com.mhwlng.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mhwlng.authservice.controller.AuthController;
import com.mhwlng.authservice.service.AuthService;
import com.mhwlng.authservice.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthServiceApplicationTests {

    @Autowired
    private AuthController authController;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Test
    public void contextLoads() throws Exception {
        assertThat(authController).isNotNull();
        assertThat(authService).isNotNull();
        assertThat(userService).isNotNull();
      }


}


