package com.movie.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Response body returned when a Rating is created or queried.
 */
@Data
@Builder
public class RatingResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long movieId;
    private String movieTitle;
    private Integer score;
}
