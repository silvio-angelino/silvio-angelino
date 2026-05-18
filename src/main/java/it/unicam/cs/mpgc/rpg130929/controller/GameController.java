package it.unicam.cs.mpgc.rpg130929.controller;

import it.unicam.cs.mpgc.rpg130929.interfaces.GameRepository;
import it.unicam.cs.mpgc.rpg130929.model.*;
import it.unicam.cs.mpgc.rpg130929.repository.GameDataLoader;

import java.util.*;
import java.util.stream.Collectors;

public class GameController {

    private final GameRepository repository;
    private final GameDataLoader dataLoader;
    private Journalist journalist;
    private final Map<String, Location> locations;
    private final Map<String, NPC> npcs;
    private final Map<String, Clue> clues;
    private Location currentLocation;

    public GameController(GameRepository repository) {
        if (repository == null) throw new IllegalArgumentException("Repository non valido");
        this.repository = repository;
        this.dataLoader = new GameDataLoader();
        this.locations = new HashMap<>();
        this.npcs = new HashMap<>();
        this.clues = new HashMap<>();
        initializeGame();
    }

    private void initializeGame() {
        journalist = new Journalist("Silvio");
        loadClues();
        loadLocations();
        loadNpcs();
        currentLocation = locations.get("redazione");
        journalist.visitLocation(currentLocation);
    }

    private void loadClues() {
        dataLoader.loadClues().forEach(clue -> clues.put(clue.getId(), clue));
    }

    private void loadLocations() {
        dataLoader.loadLocations().forEach(location -> {
            clues.values().stream()
                    .filter(clue -> clue.getLocationId() != null &&
                            clue.getLocationId().equals(location.getId()))
                    .forEach(location::addClue);
            locations.put(location.getId(), location);
        });
    }

    private void loadNpcs() {
        dataLoader.loadNpcs().forEach(npcData -> {
            NPC npc = new NPC(npcData.id, npcData.name, npcData.role);
            npcData.dialogues.forEach(npc::addDialogue);
            npcData.clueIds.stream()
                    .map(clues::get)
                    .filter(Objects::nonNull)
                    .forEach(npc::addClue);
            npcs.put(npc.getId(), npc);
        });
    }

    public Journalist getJournalist() { return journalist; }
    public Location getCurrentLocation() { return currentLocation; }

    public Collection<Location> getAllLocations() {
        return Collections.unmodifiableCollection(locations.values());
    }

    public List<NPC> getNpcsInCurrentLocation() {
        return dataLoader.loadNpcs().stream()
                .filter(data -> data.locationId.equals(currentLocation.getId()))
                .map(data -> npcs.get(data.id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

    public void collectAllCluesInCurrentLocation() {
        currentLocation.getClues().stream()
                .filter(clue -> !clue.isDiscovered())
                .forEach(this::collectClue);
    }

    public Article createArticle(String title) {
        if (title == null || title.isEmpty())
            throw new IllegalArgumentException("Titolo non valido");
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

    public int getTotalClues() {
        return clues.size();
    }

    public int getDiscoveredCluesCount() {
        return (int) clues.values().stream()
                .filter(Clue::isDiscovered)
                .count();
    }
}