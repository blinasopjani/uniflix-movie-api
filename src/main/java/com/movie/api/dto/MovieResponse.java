package com.movie.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Response body returned when a Movie is queried.
 * Includes average rating computed from the ratings table.
 */
@Data
@Builder
public class MovieResponse {
    private Long id;
    private String title;
    private String genre;
    private String description;
    private Integer releaseYear;
    private String imageUrl;
    private String imdbRating;
    private String imdbVotes;
    private String type;
    private String externalApiId;
    private Double averageRating;
}
