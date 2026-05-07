package com.movie.api.repository;

import com.movie.api.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByMovieId(Long movieId);

    List<Rating> findByUserId(Long userId);

    Optional<Rating> findByUserIdAndMovieId(Long userId, Long movieId);

    /**
     * Calculate the average score for a given movie.
     */
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.movie.id = :movieId")
    Double findAverageScoreByMovieId(Long movieId);
}
