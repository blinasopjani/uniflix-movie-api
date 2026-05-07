package com.movie.api.service;

import com.movie.api.dto.RatingRequest;
import com.movie.api.dto.RatingResponse;
import com.movie.api.entity.Movie;
import com.movie.api.entity.Rating;
import com.movie.api.entity.User;
import com.movie.api.exception.ResourceNotFoundException;
import com.movie.api.repository.MovieRepository;
import com.movie.api.repository.RatingRepository;
import com.movie.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    @Transactional
    public RatingResponse addRating(RatingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));
        
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie", request.getMovieId()));

        // Check if rating already exists, if so update it, otherwise create new
        Rating rating = ratingRepository.findByUserIdAndMovieId(user.getId(), movie.getId())
                .orElse(Rating.builder().user(user).movie(movie).build());
        
        rating.setScore(request.getScore());
        
        Rating savedRating = ratingRepository.save(rating);
        return mapToResponse(savedRating);
    }

    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByMovie(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new ResourceNotFoundException("Movie", movieId);
        }
        return ratingRepository.findByMovieId(movieId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private RatingResponse mapToResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .userId(rating.getUser().getId())
                .userName(rating.getUser().getName())
                .movieId(rating.getMovie().getId())
                .movieTitle(rating.getMovie().getTitle())
                .score(rating.getScore())
                .build();
    }
}
