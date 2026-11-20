package com.mhwlng.taskservice.controller;

import com.mhwlng.taskservice.dto.TaskPageResponseDTO;
import com.mhwlng.taskservice.dto.TaskRequestDTO;
import com.mhwlng.taskservice.dto.TaskResponseDTO;
import com.mhwlng.taskservice.entity.Task;
import com.mhwlng.taskservice.mapper.TaskMapper;
import com.mhwlng.taskservice.service.TaskService;
import jakarta.validation.Valid;
//import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController // @Controller and @ResponseBody
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Get all tasks")
    @GetMapping
    public ResponseEntity<TaskPageResponseDTO> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        TaskPageResponseDTO taskPage = taskService.getAllTasks(pageable);

        return new ResponseEntity<>(taskPage, HttpStatus.OK);
    }


    @Operation(summary = "Get task by id")
    @GetMapping("/{id}")
    public TaskResponseDTO getTaskById (@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @Operation(summary = "Create task")
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO task) {
        TaskResponseDTO savedTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    @Operation(summary = "Update task")
    @PutMapping("/{id}")
    public TaskResponseDTO updateTask (@PathVariable Long id,
                            @Valid @RequestBody TaskRequestDTO updatedTask) {
        return taskService.updateTask(id, updatedTask);
    }

    @Operation(summary = "Delete task")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Search tasks")
    @GetMapping("/search")
    public ResponseEntity<TaskPageResponseDTO> searchTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        TaskPageResponseDTO taskPage;

        if (title != null && completed != null) {
            // Filter by both
            taskPage = taskService.searchTasksByTitleAndCompletion(
                    title, completed, pageable
            );
        } else if (title != null) {
            // Filter by title only
            taskPage = taskService.searchTasksByTitle(title, pageable);
        } else if (completed != null) {
            // Filter completion only
            taskPage = taskService.getTasksByCompletion(completed, pageable);
        } else {
            taskPage = taskService.getAllTasks(pageable);
        }

        return new ResponseEntity<>(taskPage, HttpStatus.OK);
    }

}
