package com.movie.api.service;

import com.movie.api.dto.OmdbMovieResponse;
import com.movie.api.entity.Movie;
import com.movie.api.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class MovieSyncService {

    private final WebClient webClient;
    private final MovieRepository movieRepository;
    private final TranslationService translationService;

    @Value("${omdb.api.key}")
    private String apiKey;

    public MovieSyncService(WebClient.Builder webClientBuilder, MovieRepository movieRepository, TranslationService translationService) {
        this.webClient = webClientBuilder.baseUrl("https://www.omdbapi.com/").build();
        this.movieRepository = movieRepository;
        this.translationService = translationService;
    }

    public Mono<Movie> syncMovieByTitle(String title) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("t", title)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(OmdbMovieResponse.class)
                .flatMap(response -> {
                    if ("True".equals(response.getResponse())) {
                        if (movieRepository.findByExternalApiId(response.getImdbId()).isPresent()) {
                            return Mono.just(movieRepository.findByExternalApiId(response.getImdbId()).get());
                        }

                        return translationService.translateText(response.getPlot())
                                .map(translatedPlot -> {
                                    Movie movie = new Movie();
                                    movie.setTitle(response.getTitle());
                                    movie.setGenre(translationService.translateGenre(response.getGenre()));
                                    movie.setDescription(translatedPlot);
                                    movie.setImageUrl(response.getPoster());
                                    movie.setImdbRating(response.getImdbRating());
                                    movie.setImdbVotes(response.getImdbVotes());
                                    movie.setType(response.getType().equalsIgnoreCase("movie") ? "Film" : "Serial");
                                    
                                    try {
                                        String year = response.getYear().replaceAll("[^0-9]", "");
                                        if (year.length() >= 4) {
                                            movie.setReleaseYear(Integer.parseInt(year.substring(0, 4)));
                                        }
                                    } catch (Exception e) {
                                        movie.setReleaseYear(0);
                                    }
                                    
                                    movie.setExternalApiId(response.getImdbId());
                                    return movieRepository.save(movie);
                                });
                    }
                    return Mono.error(new RuntimeException("Movie not found: " + title));
                });
    }

    public void syncPopularMovies(List<String> titles) {
        titles.forEach(title -> {
            try {
                // We use block() here for simplicity during startup seeding
                syncMovieByTitle(title).block();
            } catch (Exception e) {
                System.out.println("Could not sync: " + title);
            }
        });
    }
}
