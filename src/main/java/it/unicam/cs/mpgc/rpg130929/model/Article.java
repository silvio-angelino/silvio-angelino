package it.unicam.cs.mpgc.rpg130929.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Article {

    private final String id;
    private String title;
    private String content;
    private final List<Clue> cluesUsed;
    private boolean published;

    public Article(String id, String title) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Id non valido");
        if (title == null || title.isEmpty()) throw new IllegalArgumentException("Titolo non valido");
        this.id = id;
        this.title = title;
        this.content = "";
        this.cluesUsed = new ArrayList<>();
        this.published = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public boolean isPublished() { return published; }

    public void setContent(String content) {
        if (content == null) throw new IllegalArgumentException("Contenuto non valido");
        this.content = content;
    }

    public void addClue(Clue clue) {
        if (clue == null) throw new IllegalArgumentException("Indizio non valido");
        if (!cluesUsed.contains(clue)) cluesUsed.add(clue);
    }

    public void publish() {
        if (cluesUsed.isEmpty()) throw new IllegalStateException("Non puoi pubblicare un articolo senza indizi");
        this.published = true;
    }

    public List<Clue> getCluesUsed() {
        return Collections.unmodifiableList(cluesUsed);
    }

    public int getReputationValue() {
        return cluesUsed.size() * 10;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Article)) return false;
        Article other = (Article) obj;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
