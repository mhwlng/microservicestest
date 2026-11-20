package com.mhwlng.taskservice.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(name = "TaskPageResponse", description = "Paginated task response")
@Builder
public record TaskPageResponseDTO(

        @Schema(description = "Zero-based index of the current page", example = "0")
        int currentPage,

        @Schema(description = "Total number of matching tasks", example = "42")
        long totalItems,

        @Schema(description = "Total number of pages", example = "5")
        int totalPages,

        @Schema(description = "Whether another page is available", example = "true")
        boolean hasNext,

        @Schema(description = "Whether a previous page is available", example = "false")
        boolean hasPrevious,

        @ArraySchema(schema = @Schema(implementation = TaskResponseDTO.class))
        List<TaskResponseDTO> tasks)


{
}
