package com.movie.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;

@Service
public class TranslationService {

    private final WebClient webClient;
    private final Map<String, String> genreMap;

    public TranslationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://translate.googleapis.com/translate_a/single").build();
        
        genreMap = new HashMap<>();
        genreMap.put("Action", "Aksion");
        genreMap.put("Adventure", "Aventurë");
        genreMap.put("Animation", "Animacion");
        genreMap.put("Comedy", "Komedi");
        genreMap.put("Crime", "Krim");
        genreMap.put("Documentary", "Dokumentar");
        genreMap.put("Drama", "Dramë");
        genreMap.put("Family", "Familjar");
        genreMap.put("Fantasy", "Fantazi");
        genreMap.put("History", "Histori");
        genreMap.put("Horror", "Horror");
        genreMap.put("Music", "Muzikë");
        genreMap.put("Mystery", "Mister");
        genreMap.put("Romance", "Romancë");
        genreMap.put("Sci-Fi", "Shkencë-Fiksion");
        genreMap.put("Thriller", "Triler");
        genreMap.put("War", "Luftë");
        genreMap.put("Western", "Uestern");
    }

    public String translateGenre(String englishGenres) {
        if (englishGenres == null) return "";
        String[] genres = englishGenres.split(", ");
        StringBuilder translated = new StringBuilder();
        for (int i = 0; i < genres.length; i++) {
            translated.append(genreMap.getOrDefault(genres[i], genres[i]));
            if (i < genres.length - 1) translated.append(", ");
        }
        return translated.toString();
    }

    public Mono<String> translateText(String text) {
        if (text == null || text.isEmpty()) return Mono.just(text);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("client", "gtx")
                        .queryParam("sl", "en")
                        .queryParam("tl", "sq")
                        .queryParam("dt", "t")
                        .queryParam("q", text)
                        .build())
                .retrieve()
                .bodyToMono(Object[].class)
                .map(response -> {
                    if (response != null && response.length > 0) {
                        Object firstPart = response[0];
                        if (firstPart instanceof Iterable) {
                            StringBuilder result = new StringBuilder();
                            for (Object part : (Iterable<?>) firstPart) {
                                if (part instanceof Iterable) {
                                    Object textPart = ((Iterable<?>) part).iterator().next();
                                    if (textPart instanceof String) {
                                        result.append((String) textPart);
                                    }
                                }
                            }
                            return result.toString();
                        }
                    }
                    return text;
                })
                .onErrorReturn(text);
    }
}
