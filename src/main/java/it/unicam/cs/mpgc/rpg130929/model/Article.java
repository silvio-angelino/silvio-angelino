package it.unicam.cs.mpgc.rpg130929.model;

import it.unicam.cs.mpgc.rpg130929.interfaces.Identifiable;
import it.unicam.cs.mpgc.rpg130929.interfaces.Publishable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Rappresenta un articolo scritto dal giornalista
public class Article implements Identifiable, Publishable {

    private String id;
    private String title;
    private String content;
    private List<Clue> cluesUsed;
    private boolean published;

    // costruttore vuoto serve a Gson per deserializzare
    public Article() {
        this.cluesUsed = new ArrayList<>();
        this.published = false;
    }

    public Article(String id, String title) {
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException("Id non valido");
        if (title == null || title.isEmpty())
            throw new IllegalArgumentException("Titolo non valido");
        this.id = id;
        this.title = title;
        this.content = "";
        this.cluesUsed = new ArrayList<>();
        this.published = false;
    }

    @Override
    public String getId() { return id; }

    public String getTitle() { return title; }

    public String getContent() { return content; }

    @Override
    public boolean isPublished() { return published; }

    public void setContent(String content) {
        this.content = content;
    }

    public void addClue(Clue clue) {
        if (clue == null) return;
        // evito duplicati
        if (!cluesUsed.contains(clue)) {
            cluesUsed.add(clue);
        }
    }

    @Override
    public void publish() {
        if (cluesUsed.isEmpty())
            throw new IllegalStateException(
                    "Servono almeno un indizio per pubblicare");
        this.published = true;
    }

    // copia difensiva: chi chiama non puo' modificare la lista
    // interna bypassando addClue()
    public List<Clue> getCluesUsed() {
        return Collections.unmodifiableList(cluesUsed);
    }

    // ogni indizio vale 10 punti reputazione
    public int getReputationValue() {
        return cluesUsed.size() * 10;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Article other)) return false;
        return this.id != null && this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}