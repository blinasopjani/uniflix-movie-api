package com.movie.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OmdbMovieResponse {
    @JsonProperty("Title")
    private String title;
    
    @JsonProperty("Year")
    private String year;
    
    @JsonProperty("Genre")
    private String genre;
    
    @JsonProperty("Plot")
    private String plot;
    
    @JsonProperty("imdbID")
    private String imdbId;
    
    @JsonProperty("Poster")
    private String poster;

    @JsonProperty("imdbRating")
    private String imdbRating;

    @JsonProperty("imdbVotes")
    private String imdbVotes;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Response")
    private String response;
    
    @JsonProperty("Error")
    private String error;
}
