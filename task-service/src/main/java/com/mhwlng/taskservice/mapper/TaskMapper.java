package com.mhwlng.taskservice.mapper;

import com.mhwlng.taskservice.dto.TaskPageResponseDTO;
import com.mhwlng.taskservice.dto.TaskRequestDTO;
import com.mhwlng.taskservice.dto.TaskResponseDTO;
import com.mhwlng.taskservice.entity.Task;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toEntity(TaskRequestDTO request) {

        return Task.builder()
                .title(request.title())
                .description(request.description())
                .completed(request.completed() != null ? request.completed() : false)
                .build();
    }

    public TaskResponseDTO toResponse(Task task) {

        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.getCompleted())
                .createdAt(task.getCreatedAt())
                .build();
    }

    public void updateEntityFromRequest(Task task, TaskRequestDTO request) {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setCompleted(request.completed());

    }

    public TaskPageResponseDTO updateTaskPageResponseFromRequest(List<TaskResponseDTO> tasks, Page<Task> taskPage) {

      return TaskPageResponseDTO.builder()
                .currentPage(taskPage.getNumber())
                .totalItems(taskPage.getTotalElements())
                .totalPages(taskPage.getTotalPages())
                .hasNext(taskPage.hasNext())
                .hasPrevious(taskPage.hasPrevious())
                .tasks(tasks)
                .build();
    }
}
