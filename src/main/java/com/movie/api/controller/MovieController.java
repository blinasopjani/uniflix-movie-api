package com.movie.api.controller;

import com.movie.api.dto.MovieRequest;
import com.movie.api.dto.MovieResponse;
import com.movie.api.service.MovieService;
import com.movie.api.service.MovieSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@Tag(name = "Movies", description = "Movie management endpoints")
public class MovieController {

    private final MovieService movieService;
    private final MovieSyncService movieSyncService;

    @GetMapping
    @Operation(summary = "Get all movies")
    public List<MovieResponse> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get movie by ID")
    public MovieResponse getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get movies or series by type")
    public List<MovieResponse> getMoviesByType(@PathVariable String type) {
        return movieService.getMoviesByType(type);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a movie manually")
    public MovieResponse createMovie(@Valid @RequestBody MovieRequest request) {
        return movieService.createMovie(request);
    }

    @GetMapping("/search")
    @Operation(summary = "Search movies by title in local database")
    public List<MovieResponse> searchMovies(@RequestParam String title) {
        return movieService.searchMoviesByTitle(title);
    }

    @PostMapping("/sync")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Sync a movie from OMDb API by title")
    public Mono<MovieResponse> syncMovie(@RequestParam String title) {
        return movieSyncService.syncMovieByTitle(title)
                .map(movieService::mapToResponse);
    }

    @PostMapping("/translate-all")
    @Operation(summary = "Translate all existing movies in database to Albanian")
    public void translateAll() {
        movieService.translateAllMovies();
    }
}
