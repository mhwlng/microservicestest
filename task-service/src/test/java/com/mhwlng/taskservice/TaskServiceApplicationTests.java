package com.mhwlng.taskservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.mhwlng.taskservice.controller.TaskController;
import com.mhwlng.taskservice.service.TaskService;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("testdb")
@SpringBootTest
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

}
