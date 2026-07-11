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
    private List<String> clueIds;
    private boolean published;

    // costruttore vuoto serve a Gson per deserializzare
    public Article() {
        this.clueIds = new ArrayList<>();
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
        this.clueIds = new ArrayList<>();
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

    // salvo solo l'id dell'indizio, non l'oggetto intero: evita di
    // duplicare i dati dell'indizio nel file di salvataggio
    // (lo stesso Clue e' gia' presente per intero nel notebook)
    public void addClue(Clue clue) {
        if (clue == null || clue.getId() == null) return;
        if (!clueIds.contains(clue.getId())) {
            clueIds.add(clue.getId());
        }
    }

    @Override
    public void publish() {
        if (clueIds.isEmpty())
            throw new IllegalStateException(
                    "Servono almeno un indizio per pubblicare");
        this.published = true;
    }

    // copia difensiva: chi chiama non puo' modificare la lista
    // interna bypassando addClue()
    public List<String> getClueIds() {
        return Collections.unmodifiableList(clueIds);
    }

    // ogni indizio vale 10 punti reputazione
    public int getReputationValue() {
        return clueIds.size() * 10;
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