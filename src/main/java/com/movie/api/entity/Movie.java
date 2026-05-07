package com.movie.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a movie in the database.
 * May be added manually (POST /movies) or synced from OMDb API.
 */
@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Column
    private String genre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private Integer releaseYear;

    @Column(length = 500)
    private String imageUrl;

    @Column
    private String imdbRating;

    @Column
    private String imdbVotes;

    @Column
    private String type; // "movie" ose "series"

    /**
     * External ID from OMDb (imdbID) — used to avoid duplicate syncing.
     */
    @Column(unique = true)
    private String externalApiId;

    @Builder.Default
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();
}
