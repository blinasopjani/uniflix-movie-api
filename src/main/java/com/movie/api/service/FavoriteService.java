package com.movie.api.service;

import com.movie.api.dto.MovieResponse;
import com.movie.api.entity.Favorite;
import com.movie.api.entity.Movie;
import com.movie.api.entity.User;
import com.movie.api.exception.ConflictException;
import com.movie.api.exception.ResourceNotFoundException;
import com.movie.api.repository.FavoriteRepository;
import com.movie.api.repository.MovieRepository;
import com.movie.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieService movieService;

    @Transactional
    public void addFavorite(Long userId, Long movieId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", movieId));

        if (favoriteRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new ConflictException("Movie already in favorites");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .movie(movie)
                .build();
        
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long userId, Long movieId) {
        if (!favoriteRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new ResourceNotFoundException("Favorite link not found");
        }
        favoriteRepository.deleteByUserIdAndMovieId(userId, movieId);
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> getUserFavorites(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
        return favoriteRepository.findByUserId(userId).stream()
                .map(favorite -> movieService.mapToResponse(favorite.getMovie()))
                .toList();
    }
}
