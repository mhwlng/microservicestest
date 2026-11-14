package com.mhwlng.taskservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TaskResponseDTO(
        Long id,
        String title,
        String description,
        Boolean completed,
        LocalDateTime createdAt) {
}
