package com.movie.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request body for creating or importing a Movie.
 */
@Data
public class MovieRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String genre;
    private String description;
    private Integer releaseYear;
    private String imageUrl;
}
