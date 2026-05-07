package com.movie.api.controller;

import com.movie.api.dto.RatingRequest;
import com.movie.api.dto.RatingResponse;
import com.movie.api.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
@Tag(name = "Ratings", description = "Movie rating endpoints")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add or update a movie rating")
    public RatingResponse addRating(@Valid @RequestBody RatingRequest request) {
        return ratingService.addRating(request);
    }

    @GetMapping("/movies/{movieId}")
    @Operation(summary = "Get all ratings for a specific movie")
    public List<RatingResponse> getRatingsByMovie(@PathVariable Long movieId) {
        return ratingService.getRatingsByMovie(movieId);
    }
}
