package it.unicam.cs.mpgc.rpg130929.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// classe di utilità per le statistiche del gioco
// usa le Stream API per fare calcoli sui dati
public class GameStats {

    private final Journalist journalist;

    public GameStats(Journalist journalist) {
        if (journalist == null)
            throw new IllegalArgumentException("Giornalista non valido");
        this.journalist = journalist;
    }

    // conta quanti indizi sono stati scoperti
    public long countDiscoveredClues() {
        return journalist.getNotebook().stream()
                .filter(Clue::isDiscovered)
                .count();
    }

    // somma la reputazione derivata solo dagli articoli pubblicati
    // (non e' il totale: la reputazione del giornalista include
    // anche il bonus dato dal completamento delle missioni)
    public int calculateReputationFromArticles() {
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

    // raggruppa gli indizi in scoperti e non scoperti
    public Map<Boolean, List<Clue>> groupCluesByDiscovery() {
        return journalist.getNotebook().stream()
                .collect(Collectors.partitioningBy(
                        Clue::isDiscovered));
    }

    public boolean hasEnoughCluesForArticle(int minClues) {
        return journalist.getNotebook().stream()
                .filter(Clue::isDiscovered)
                .count() >= minClues;
    }

    public String getSummary() {
        return "Giornalista: " + journalist.getName() + "\n" +
                "Indizi raccolti: " + countDiscoveredClues() + "\n" +
                "Articoli pubblicati: " +
                getPublishedArticles().size() + "\n" +
                "Reputazione totale: " +
                journalist.getReputation();
    }
}
