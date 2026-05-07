package com.movie.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request body for creating a new User.
 */
@Data
public class UserRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    private String email;

    private String password;
    private String avatarUrl;
}
