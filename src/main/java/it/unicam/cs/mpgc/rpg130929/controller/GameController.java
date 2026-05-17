package it.unicam.cs.mpgc.rpg130929.controller;

import it.unicam.cs.mpgc.rpg130929.interfaces.GameRepository;
import it.unicam.cs.mpgc.rpg130929.model.*;

import java.util.*;

public class GameController {

    private final GameRepository repository;
    private Journalist journalist;
    private final Map<String, Location> locations;
    private final Map<String, NPC> npcs;
    private Location currentLocation;

    public GameController(GameRepository repository) {
        if (repository == null) throw new IllegalArgumentException("Repository non valido");
        this.repository = repository;
        this.locations = new HashMap<>();
        this.npcs = new HashMap<>();
        initializeGame();
    }

    private void initializeGame() {
        journalist = new Journalist("Silvio");

        Location redazione = new Location("redazione", "Redazione del Giornale",
                "Il tuo posto di lavoro. Qui scrivi i tuoi articoli.");
        Location porto = new Location("porto", "Il Porto",
                "Un luogo misterioso. Molti affari loschi avvengono qui.");
        Location mercato = new Location("mercato", "Il Mercato",
                "Sempre affollato. Ottimo posto per raccogliere voci.");

        Clue clue1 = new Clue("c1", "Una lettera anonima che accusa il sindaco", "porto");
        Clue clue2 = new Clue("c2", "Tracce di contrabbando sul molo", "porto");
        Clue clue3 = new Clue("c3", "Un testimone ha visto qualcosa di strano", "mercato");

        porto.addClue(clue1);
        porto.addClue(clue2);
        mercato.addClue(clue3);

        NPC informatore = new NPC("n1", "Vecchio Mario", "Informatore");
        informatore.addDialogue("Psst... ho sentito cose strane al porto...");
        informatore.addClue(clue3);

        locations.put(redazione.getId(), redazione);
        locations.put(porto.getId(), porto);
        locations.put(mercato.getId(), mercato);
        npcs.put(informatore.getId(), informatore);

        currentLocation = redazione;
        journalist.visitLocation(redazione);
    }

    public Journalist getJournalist() { return journalist; }
    public Location getCurrentLocation() { return currentLocation; }

    public Collection<Location> getAllLocations() {
        return Collections.unmodifiableCollection(locations.values());
    }

    public void moveToLocation(String locationId) {
        Location location = locations.get(locationId);
        if (location == null) throw new IllegalArgumentException("Luogo non trovato");
        currentLocation = location;
        journalist.visitLocation(location);
    }

    public void collectClue(Clue clue) {
        if (clue == null) throw new IllegalArgumentException("Indizio non valido");
        journalist.addClueToNotebook(clue);
    }

    public Article createArticle(String title) {
        if (title == null || title.isEmpty()) throw new IllegalArgumentException("Titolo non valido");
        Article article = new Article("a" + System.currentTimeMillis(), title);
        journalist.addArticle(article);
        return article;
    }

    public void publishArticle(Article article) {
        journalist.publishArticle(article);
        saveGame();
    }

    public void saveGame() {
        GameState state = new GameState(journalist, currentLocation.getId());
        repository.saveGame(state);
    }

    public boolean hasSavedGame() {
        return repository.hasSavedGame();
    }
}
