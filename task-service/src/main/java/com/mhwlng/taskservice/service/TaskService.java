package com.mhwlng.taskservice.service;

import com.mhwlng.taskservice.dto.TaskRequestDTO;
import com.mhwlng.taskservice.dto.TaskResponseDTO;
import com.mhwlng.taskservice.entity.Task;
import com.mhwlng.taskservice.exception.TaskNotFoundException;
import com.mhwlng.taskservice.mapper.TaskMapper;
import com.mhwlng.taskservice.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository,
                       TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public Page<Task> searchTasksByTitle(String title, Pageable pageable) {
        return taskRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    public Page<Task> searchTasksByTitleAndCompletion(String title,
                                                      Boolean completed,
                                                      Pageable pageable) {
        return taskRepository.findByTitleContainingAndCompleted(title,
                completed, pageable);
    }

    public Page<Task> getTasksByCompletion(Boolean completed, Pageable pageable) {
        return taskRepository.findByCompleted(completed, pageable);
    }

    public Page<Task> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    public Page<TaskResponseDTO> getTasksByCompletionStatus(boolean status,
                                                         Pageable pageable) {
        final Page<Task> completedTasks = taskRepository.findByCompleted(status, pageable);
        return completedTasks.map(taskMapper::toResponse);
    }

    public TaskResponseDTO getTaskById(Long id) {
        Task retrievedTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return taskMapper.toResponse(retrievedTask);
    }

    public TaskResponseDTO createTask(TaskRequestDTO task) {
        Task entityTask = taskMapper.toEntity(task);
        Task savedTask = taskRepository.save(entityTask);
        return taskMapper.toResponse(savedTask);
    }

    public TaskResponseDTO updateTask(Long id, TaskRequestDTO updatedTask) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        taskMapper.updateEntityFromRequest(task, updatedTask);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.delete(task);
    }



}
