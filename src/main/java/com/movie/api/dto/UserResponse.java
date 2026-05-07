package com.movie.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Response body returned when a User is created or queried.
 */
@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String avatarUrl;
}
