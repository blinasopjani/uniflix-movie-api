package com.movie.api.controller;

import com.movie.api.dto.MovieResponse;
import com.movie.api.dto.UserResponse;
import com.movie.api.service.FavoriteService;
import com.movie.api.service.RecommendationService;
import com.movie.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management, favorites and recommendations")
public class UserController {

    private final UserService userService;
    private final FavoriteService favoriteService;
    private final RecommendationService recommendationService;

    @GetMapping("/{id}")
    @Operation(summary = "Get user details")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/{id}/favorites")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a movie to user favorites")
    public void addFavorite(@PathVariable Long id, @RequestParam Long movieId) {
        favoriteService.addFavorite(id, movieId);
    }

    @DeleteMapping("/{id}/favorites")
    @Operation(summary = "Remove a movie from user favorites")
    public void removeFavorite(@PathVariable Long id, @RequestParam Long movieId) {
        favoriteService.removeFavorite(id, movieId);
    }

    @GetMapping("/{id}/favorites")
    @Operation(summary = "Get user favorite movies")
    public List<MovieResponse> getFavorites(@PathVariable Long id) {
        return favoriteService.getUserFavorites(id);
    }

    @GetMapping("/{id}/recommendations")
    @Operation(summary = "Get movie recommendations for a user")
    public List<MovieResponse> getRecommendations(@PathVariable Long id) {
        return recommendationService.getRecommendations(id);
    }
}
