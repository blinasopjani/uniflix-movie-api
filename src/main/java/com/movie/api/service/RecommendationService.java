package com.movie.api.service;

import com.movie.api.dto.MovieResponse;
import com.movie.api.repository.FavoriteRepository;
import com.movie.api.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final MovieRepository movieRepository;
    private final FavoriteRepository favoriteRepository;
    private final MovieService movieService;

    @Transactional(readOnly = true)
    public List<MovieResponse> getRecommendations(Long userId) {
        // 1. Get user's favorite genres
        Set<String> favoriteGenres = favoriteRepository.findByUserId(userId).stream()
                .map(f -> f.getMovie().getGenre())
                .filter(g -> g != null && !g.isEmpty())
                .flatMap(g -> Arrays.stream(g.split(",")))
                .map(String::trim)
                .collect(Collectors.toSet());

        if (favoriteGenres.isEmpty()) {
            // Default: highest rated movies if no favorites
            return movieService.getAllMovies().stream()
                    .sorted((m1, m2) -> Double.compare(m2.getAverageRating(), m1.getAverageRating()))
                    .limit(5)
                    .toList();
        }

        // 2. Find movies with those genres
        List<MovieResponse> recommended = movieRepository.findAll().stream()
                .filter(movie -> {
                    if (movie.getGenre() == null) return false;
                    return Arrays.stream(movie.getGenre().split(","))
                            .map(String::trim)
                            .anyMatch(favoriteGenres::contains);
                })
                .map(movieService::mapToResponse)
                // 3. Filter out what they already favorited
                .filter(mr -> !isAlreadyFavorited(userId, mr.getId()))
                // 4. Sort by rating
                .sorted((m1, m2) -> Double.compare(m2.getAverageRating(), m1.getAverageRating()))
                .limit(10)
                .toList();

        return recommended;
    }

    private boolean isAlreadyFavorited(Long userId, Long movieId) {
        return favoriteRepository.existsByUserIdAndMovieId(userId, movieId);
    }
}
