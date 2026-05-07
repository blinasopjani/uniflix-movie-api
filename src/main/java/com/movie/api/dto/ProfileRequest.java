package com.movie.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    private String avatarUrl;
}
