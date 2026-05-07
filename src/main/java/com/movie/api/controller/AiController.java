package com.movie.api.controller;

import com.movie.api.dto.MovieResponse;
import com.movie.api.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Tag(name = "AI Bot", description = "AI Movie Recommendation Bot")
public class AiController {

    private final MovieService movieService;

    @PostMapping("/chat")
    @Operation(summary = "Chat with AI bot for recommendations")
    public Map<String, Object> chat(@RequestBody Map<String, String> payload) {
        String message = payload.getOrDefault("message", "").toLowerCase();
        List<MovieResponse> allMovies = movieService.getAllMovies();
        
        String responseMessage;
        List<MovieResponse> recommendedMovies;

        if (message.contains("aksion") || message.contains("action")) {
            recommendedMovies = filterByGenre(allMovies, "Aksion");
            responseMessage = "Këtu janë disa filma aksion që mund t'ju pëlqejnë!";
        } else if (message.contains("komedi") || message.contains("comedy")) {
            recommendedMovies = filterByGenre(allMovies, "Komedi");
            responseMessage = "Për të qeshur? Provojini këto komedi!";
        } else if (message.contains("horror")) {
            recommendedMovies = filterByGenre(allMovies, "Horror");
            responseMessage = "Gati për t'u frikësuar? Këto janë zgjedhjet e mia horror.";
        } else if (message.contains("dramë") || message.contains("drama")) {
            recommendedMovies = filterByGenre(allMovies, "Dramë");
            responseMessage = "Disa drama emocionuese për ju.";
        } else if (message.contains("serial") || message.contains("series")) {
            recommendedMovies = allMovies.stream()
                    .filter(m -> "Serial".equalsIgnoreCase(m.getType()))
                    .limit(5)
                    .toList();
            responseMessage = "Ja disa nga serialet më të mira!";
        } else {
            Collections.shuffle(allMovies);
            recommendedMovies = allMovies.stream().limit(3).toList();
            responseMessage = "Përshëndetje! Unë jam UniAI. Mund të më pyesni për filma aksion, komedi, horror ose thjesht kërkoni një rekomandim!";
        }

        return Map.of(
            "response", responseMessage,
            "movies", recommendedMovies
        );
    }

    private List<MovieResponse> filterByGenre(List<MovieResponse> movies, String genre) {
        return movies.stream()
                .filter(m -> m.getGenre() != null && m.getGenre().toLowerCase().contains(genre.toLowerCase()))
                .limit(5)
                .toList();
    }
}
