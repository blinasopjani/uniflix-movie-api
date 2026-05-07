package com.movie.api.repository;

import com.movie.api.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByTitleContainingIgnoreCase(String title);

    List<Movie> findByGenreIgnoreCase(String genre);

    List<Movie> findByTypeIgnoreCase(String type);

    Optional<Movie> findByExternalApiId(String externalApiId);

    boolean existsByExternalApiId(String externalApiId);
}
