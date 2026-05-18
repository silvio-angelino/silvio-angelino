package it.unicam.cs.mpgc.rpg130929.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rappresenta il giornalista investigativo controllato dal giocatore.
 * Estende {@link GameCharacter} e gestisce il taccuino degli indizi,
 * gli articoli scritti e i luoghi visitati.
 * La reputazione aumenta ogni volta che viene pubblicato un articolo.
 *
 * @author Silvio Angelino
 * @version 1.0
 */
public class Journalist extends GameCharacter {

    private int reputation;
    private final List<Clue> notebook;
    private final List<Article> articles;
    private final List<Location> visitedLocations;

    /**
     * Costruttore vuoto necessario per la deserializzazione JSON.
     */
    public Journalist() {
        super();
        this.notebook = new ArrayList<>();
        this.articles = new ArrayList<>();
        this.visitedLocations = new ArrayList<>();
    }

    /**
     * Costruisce un giornalista con il nome specificato.
     *
     * @param name il nome del giornalista
     * @throws IllegalArgumentException se il nome è nullo o vuoto
     */
    public Journalist(String name) {
        super(name, "Giornalista");
        this.reputation = 0;
        this.notebook = new ArrayList<>();
        this.articles = new ArrayList<>();
        this.visitedLocations = new ArrayList<>();
    }

    @Override
    public void presentati() {
        System.out.println("Sono " + getName() +
                ", giornalista investigativo.");
    }

    /**
     * Restituisce la reputazione attuale del giornalista.
     *
     * @return il valore della reputazione
     */
    public int getReputation() { return reputation; }

    /**
     * Aggiunge un indizio al taccuino del giornalista.
     * Se l'indizio non è già presente, viene aggiunto e marcato
     * come scoperto.
     *
     * @param clue l'indizio da aggiungere
     * @throws IllegalArgumentException se l'indizio è nullo
     */
    public void addClueToNotebook(Clue clue) {
        if (clue == null)
            throw new IllegalArgumentException("Indizio non valido");
        if (!notebook.contains(clue)) {
            notebook.add(clue);
            clue.discover();
        }
    }

    /**
     * Aggiunge un articolo alla lista degli articoli del giornalista.
     *
     * @param article l'articolo da aggiungere
     * @throws IllegalArgumentException se l'articolo è nullo
     */
    public void addArticle(Article article) {
        if (article == null)
            throw new IllegalArgumentException("Articolo non valido");
        if (!articles.contains(article)) articles.add(article);
    }

    /**
     * Pubblica un articolo e aggiorna la reputazione del giornalista.
     *
     * @param article l'articolo da pubblicare
     * @throws IllegalArgumentException se l'articolo è nullo
     */
    public void publishArticle(Article article) {
        if (article == null)
            throw new IllegalArgumentException("Articolo non valido");
        article.publish();
        reputation += article.getReputationValue();
    }

    /**
     * Registra la visita a un luogo.
     * Se il luogo non è già stato visitato, viene aggiunto
     * alla lista e marcato come visitato.
     *
     * @param location il luogo da visitare
     * @throws IllegalArgumentException se il luogo è nullo
     */
    public void visitLocation(Location location) {
        if (location == null)
            throw new IllegalArgumentException("Luogo non valido");
        if (!visitedLocations.contains(location)) {
            visitedLocations.add(location);
            location.visit();
        }
    }

    /**
     * Restituisce una vista non modificabile del taccuino.
     *
     * @return lista non modificabile degli indizi raccolti
     */
    public List<Clue> getNotebook() {
        return Collections.unmodifiableList(notebook);
    }

    /**
     * Restituisce una vista non modificabile degli articoli.
     *
     * @return lista non modificabile degli articoli scritti
     */
    public List<Article> getArticles() {
        return Collections.unmodifiableList(articles);
    }

    /**
     * Restituisce una vista non modificabile dei luoghi visitati.
     *
     * @return lista non modificabile dei luoghi visitati
     */
    public List<Location> getVisitedLocations() {
        return Collections.unmodifiableList(visitedLocations);
    }
}