package com.mhwlng.taskservice.controller;

import com.mhwlng.taskservice.dto.TaskRequestDTO;
import com.mhwlng.taskservice.dto.TaskResponseDTO;
import com.mhwlng.taskservice.entity.Task;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // @Controller and @ResponseBody
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    //Constructor Injection
    //@Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Search tasks")
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchTasks(
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

        Page<Task> taskPage;

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

        List<TaskResponseDTO> tasks = taskPage.getContent()
                .stream()
                .map(task -> new TaskResponseDTO(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getCompleted(),
                        task.getCreatedAt()                        
                      ))
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("tasks", tasks);
        response.put("currentPage", taskPage.getNumber());
        response.put("totalItems", taskPage.getTotalElements());
        response.put("totalPages", taskPage.getTotalPages());
        response.put("hasNext", taskPage.hasNext());
        response.put("hasPrevious", taskPage.hasPrevious());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "Get all tasks")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task> taskPage = taskService.getAllTasks(pageable);

        List<TaskResponseDTO> tasks = taskPage.getContent()
                .stream()
                .map(task -> new TaskResponseDTO(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getCompleted(),
                        task.getCreatedAt()
                      ))
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("tasks", tasks);
        response.put("currentPage", taskPage.getNumber());
        response.put("totalItems", taskPage.getTotalElements());
        response.put("totalPages", taskPage.getTotalPages());
        response.put("hasNext", taskPage.hasNext());
        response.put("hasPrevious", taskPage.hasPrevious());

        return new ResponseEntity<>(response, HttpStatus.OK);
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

    @Operation(summary = "Get tasks by completion status")
    @GetMapping("/completed/{status}")
    public List<TaskResponseDTO> getTasksByCompletions(@PathVariable boolean status) {
        return taskService.getTasksByCompletionStatus(status);
    }

    @Operation(summary = "Search tasks by title")
    @GetMapping("/search-by-title")
    public List<TaskResponseDTO> searchTasksByTitle(@RequestParam String title) {
        return taskService.searchTasksByTitle(title);
    }

}
