package com.movie.api.service;

import com.movie.api.dto.MovieRequest;
import com.movie.api.dto.MovieResponse;
import com.movie.api.entity.Movie;
import com.movie.api.exception.ResourceNotFoundException;
import com.movie.api.repository.MovieRepository;
import com.movie.api.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class MovieService {

    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;
    private final TranslationService translationService;

    @Transactional
    public void translateAllMovies() {
        movieRepository.findAll().forEach(movie -> {
            movie.setGenre(translationService.translateGenre(movie.getGenre()));
            String translatedPlot = translationService.translateText(movie.getDescription()).block();
            movie.setDescription(translatedPlot);
            if (movie.getType() != null) {
                if (movie.getType().equalsIgnoreCase("movie")) movie.setType("Film");
                else if (movie.getType().equalsIgnoreCase("series")) movie.setType("Serial");
            }
            movieRepository.save(movie);
        });
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", id));
        return mapToResponse(movie);
    }

    @Transactional
    public MovieResponse createMovie(MovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .genre(request.getGenre())
                .description(request.getDescription())
                .releaseYear(request.getReleaseYear())
                .imageUrl(request.getImageUrl())
                .build();
        
        Movie savedMovie = movieRepository.save(movie);
        return mapToResponse(savedMovie);
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> searchMoviesByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> getMoviesByType(String type) {
        String searchType = type;
        if (type.equalsIgnoreCase("movie")) searchType = "Film";
        else if (type.equalsIgnoreCase("series")) searchType = "Serial";
        
        return movieRepository.findByTypeIgnoreCase(searchType).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public MovieResponse mapToResponse(Movie movie) {
        Double avgRating = ratingRepository.findAverageScoreByMovieId(movie.getId());
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .genre(movie.getGenre())
                .description(movie.getDescription())
                .releaseYear(movie.getReleaseYear())
                .imageUrl(movie.getImageUrl())
                .imdbRating(movie.getImdbRating())
                .imdbVotes(movie.getImdbVotes())
                .type(movie.getType())
                .externalApiId(movie.getExternalApiId())
                .averageRating(avgRating != null ? avgRating : 0.0)
                .build();
    }
}
