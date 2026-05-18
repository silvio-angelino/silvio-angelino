package it.unicam.cs.mpgc.rpg130929;

import it.unicam.cs.mpgc.rpg130929.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameStats {

    private final Journalist journalist;

    public GameStats(Journalist journalist) {
        if (journalist == null) throw new IllegalArgumentException("Giornalista non valido");
        this.journalist = journalist;
    }

    public long countDiscoveredClues() {
        return journalist.getNotebook().stream()
                .filter(Clue::isDiscovered)
                .count();
    }

    public int calculateTotalReputation() {
        return journalist.getArticles().stream()
                .filter(Article::isPublished)
                .mapToInt(Article::getReputationValue)
                .sum();
    }

    public List<Article> getPublishedArticles() {
        return journalist.getArticles().stream()
                .filter(Article::isPublished)
                .collect(Collectors.toList());
    }

    public Map<Boolean, List<Clue>> groupCluesByDiscovery() {
        return journalist.getNotebook().stream()
                .collect(Collectors.partitioningBy(Clue::isDiscovered));
    }

    public boolean hasEnoughCluesForArticle(int minClues) {
        return journalist.getNotebook().stream()
                .filter(Clue::isDiscovered)
                .count() >= minClues;
    }

    public String getSummary() {
        return String.format(
                "Giornalista: %s\n" +
                        "Indizi raccolti: %d\n" +
                        "Articoli pubblicati: %d\n" +
                        "Reputazione totale: %d",
                journalist.getName(),
                countDiscoveredClues(),
                getPublishedArticles().size(),
                calculateTotalReputation()
        );
    }
}
