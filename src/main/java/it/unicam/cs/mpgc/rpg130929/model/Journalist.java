package it.unicam.cs.mpgc.rpg130929.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Journalist {

    private final String name;
    private int reputation;
    private final List<Clue> notebook;
    private final List<Article> articles;
    private final List<Location> visitedLocations;

    public Journalist(String name) {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Nome non valido");
        this.name = name;
        this.reputation = 0;
        this.notebook = new ArrayList<>();
        this.articles = new ArrayList<>();
        this.visitedLocations = new ArrayList<>();
    }

    public String getName() { return name; }
    public int getReputation() { return reputation; }

    public void addClueToNotebook(Clue clue) {
        if (clue == null) throw new IllegalArgumentException("Indizio non valido");
        if (!notebook.contains(clue)) {
            notebook.add(clue);
            clue.discover();
        }
    }

    public void addArticle(Article article) {
        if (article == null) throw new IllegalArgumentException("Articolo non valido");
        if (!articles.contains(article)) articles.add(article);
    }

    public void publishArticle(Article article) {
        if (article == null) throw new IllegalArgumentException("Articolo non valido");
        article.publish();
        reputation += article.getReputationValue();
    }

    public void visitLocation(Location location) {
        if (location == null) throw new IllegalArgumentException("Luogo non valido");
        if (!visitedLocations.contains(location)) {
            visitedLocations.add(location);
            location.visit();
        }
    }

    public List<Clue> getNotebook() {
        return Collections.unmodifiableList(notebook);
    }

    public List<Article> getArticles() {
        return Collections.unmodifiableList(articles);
    }

    public List<Location> getVisitedLocations() {
        return Collections.unmodifiableList(visitedLocations);
    }
}
