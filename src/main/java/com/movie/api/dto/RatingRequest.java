package com.movie.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request body for submitting a rating for a movie.
 */
@Data
public class RatingRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "movieId is required")
    private Long movieId;

    @Min(value = 1, message = "Score must be between 1 and 5")
    @Max(value = 5, message = "Score must be between 1 and 5")
    @NotNull(message = "Score is required")
    private Integer score;
}
