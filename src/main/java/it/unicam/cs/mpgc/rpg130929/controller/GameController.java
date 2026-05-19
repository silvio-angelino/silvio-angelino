package it.unicam.cs.mpgc.rpg130929.controller;

import it.unicam.cs.mpgc.rpg130929.GameStats;
import it.unicam.cs.mpgc.rpg130929.interfaces.GameRepository;
import it.unicam.cs.mpgc.rpg130929.interfaces.ReputationCalculator;
import it.unicam.cs.mpgc.rpg130929.model.*;
import it.unicam.cs.mpgc.rpg130929.repository.GameDataLoader;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller principale del gioco.
 * Gestisce la logica di gioco, il movimento del personaggio,
 * la raccolta degli indizi, i dialoghi e le missioni.
 * Segue il principio DIP dipendendo da GameRepository
 * e non da una implementazione concreta.
 *
 * @author Silvio Angelino
 * @version 1.0
 */
public class GameController {

    private final GameRepository repository;
    private final GameDataLoader dataLoader;
    private final ReputationCalculator reputationCalculator;
    private Journalist journalist;
    private final Map<String, Location> locations;
    private final Map<String, NPC> npcs;
    private final Map<String, Clue> clues;
    private final Map<String, Quest> quests;
    private final Map<String, List<GameDataLoader.ChoiceData>> npcChoices;
    private Location currentLocation;
    private int suspicionLevel;
    private int daysRemaining;
    private static final int MAX_SUSPICION = 100;
    private static final int MAX_DAYS = 10;

    public GameController(GameRepository repository) {
        if (repository == null)
            throw new IllegalArgumentException("Repository non valido");
        this.repository = repository;
        this.dataLoader = new GameDataLoader();
        this.reputationCalculator = cluesCount -> cluesCount * 10;
        this.locations = new HashMap<>();
        this.npcs = new HashMap<>();
        this.clues = new HashMap<>();
        this.quests = new HashMap<>();
        this.npcChoices = new HashMap<>();
        this.suspicionLevel = 0;
        this.daysRemaining = MAX_DAYS;
        initializeGame();
    }

    private void initializeGame() {
        journalist = new Journalist("Silvio");
        loadClues();
        loadLocations();
        loadNpcs();
        loadQuests();
        currentLocation = locations.get("redazione");
        journalist.visitLocation(currentLocation);
    }

    private void loadClues() {
        dataLoader.loadClues().forEach(clue ->
                clues.put(clue.getId(), clue));
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
            if (npcData.choices != null) {
                npcChoices.put(npc.getId(), npcData.choices);
            }
        });
    }

    private void loadQuests() {
        dataLoader.loadQuests().forEach(questData -> {
            Quest quest = new Quest(
                    questData.id,
                    questData.title,
                    questData.description,
                    questData.objectives,
                    questData.experienceReward
            );
            quests.put(quest.getId(), quest);
            journalist.addQuest(quest);
        });
    }

    public Journalist getJournalist() { return journalist; }
    public Location getCurrentLocation() { return currentLocation; }

    public Collection<Location> getAllLocations() {
        return Collections.unmodifiableCollection(locations.values());
    }

    public List<NPC> getNpcsInCurrentLocation() {
        return dataLoader.loadNpcs().stream()
                .filter(data -> data.locationId
                        .equals(currentLocation.getId()))
                .map(data -> npcs.get(data.id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<GameDataLoader.ChoiceData> getChoicesForNpc(
            String npcId) {
        return npcChoices.getOrDefault(npcId, new ArrayList<>());
    }

    public void moveToLocation(String locationId) {
        Location location = locations.get(locationId);
        if (location == null)
            throw new IllegalArgumentException("Luogo non trovato");
        currentLocation = location;
        journalist.visitLocation(location);
        increaseSuspicion(5);
        advanceDay();
        checkQuestObjectives();
    }

    public void collectClue(Clue clue) {
        if (clue == null)
            throw new IllegalArgumentException("Indizio non valido");
        journalist.addClueToNotebook(clue);
        checkQuestObjectives();
    }

    public void collectAllCluesInCurrentLocation() {
        currentLocation.getClues().stream()
                .filter(clue -> !clue.isDiscovered())
                .forEach(this::collectClue);
        increaseSuspicion(10);
    }

    public Clue getClueById(String clueId) {
        return clues.get(clueId);
    }

    public DialogueResult processChoice(String npcId,
                                        GameDataLoader.ChoiceData choice) {
        boolean canUse = journalist.getStats()
                .canUseDialogueOption(choice.requiredCharisma);

        if (!canUse) {
            increaseSuspicion(5);
            return new DialogueResult(false,
                    "Il tuo carisma non e' sufficiente. "
                            + "L'NPC ti guarda con sospetto.",
                    null, 0);
        }

        journalist.getStats().addExperience(choice.experienceReward);
        decreaseSuspicion(5);

        Clue discoveredClue = null;
        if (choice.clueId != null && !choice.clueId.isEmpty()) {
            Clue clue = clues.get(choice.clueId);
            if (clue != null && !clue.isDiscovered()) {
                collectClue(clue);
                discoveredClue = clue;
            }
        }

        checkQuestObjectives();
        return new DialogueResult(true, choice.response,
                discoveredClue, choice.experienceReward);
    }

    private void checkQuestObjectives() {
        journalist.getActiveQuests().forEach(quest -> {
            updateQuestProgress(quest);
        });
    }

    private void updateQuestProgress(Quest quest) {
        int cluesFound = journalist.getNotebook().size();
        int npcsContacted = (int) npcs.values().stream()
                .filter(npc -> npc.getCluesProvided().stream()
                        .anyMatch(Clue::isDiscovered))
                .count();

        if (quest.getId().equals("q2") && cluesFound >= 2
                && currentLocation.getId().equals("porto")) {
            quest.completeObjective(0);
        }
        if (quest.getId().equals("q3") && npcsContacted >= 1) {
            quest.completeObjective(0);
        }

        if (quest.isCompleted() &&
                !journalist.getCompletedQuests().contains(quest)) {
            journalist.completeQuest(quest);
        }
    }

    public Article createArticle(String title) {
        if (title == null || title.isEmpty())
            throw new IllegalArgumentException("Titolo non valido");
        Article article = new Article(
                "a" + System.currentTimeMillis(), title);
        journalist.addArticle(article);
        return article;
    }

    public void publishArticle(Article article) {
        journalist.publishArticle(article);
        decreaseSuspicion(15);
        saveGame();
    }

    public void saveGame() {
        GameState state = new GameState(journalist,
                currentLocation.getId());
        repository.saveGame(state);
    }

    public boolean hasSavedGame() {
        return repository.hasSavedGame();
    }

    public int getTotalClues() { return clues.size(); }

    public int getDiscoveredCluesCount() {
        return (int) clues.values().stream()
                .filter(Clue::isDiscovered)
                .count();
    }

    public int calculateReputation(int cluesCount) {
        return reputationCalculator.calculate(cluesCount);
    }

    public GameStats getGameStats() {
        return new GameStats(journalist);
    }

    public Collection<Quest> getActiveQuests() {
        return journalist.getActiveQuests();
    }

    public Collection<Quest> getCompletedQuests() {
        return journalist.getCompletedQuests();
    }

    public int getSuspicionLevel() { return suspicionLevel; }
    public int getDaysRemaining() { return daysRemaining; }
    public int getMaxSuspicion() { return MAX_SUSPICION; }
    public int getMaxDays() { return MAX_DAYS; }

    public void increaseSuspicion(int amount) {
        this.suspicionLevel = Math.min(
                suspicionLevel + amount, MAX_SUSPICION);
    }

    public void decreaseSuspicion(int amount) {
        this.suspicionLevel = Math.max(
                suspicionLevel - amount, 0);
    }

    public void advanceDay() {
        if (daysRemaining > 0) daysRemaining--;
    }

    public boolean isGameOver() {
        return suspicionLevel >= MAX_SUSPICION
                || daysRemaining <= 0;
    }

    public boolean isVictory() {
        return journalist.getReputation() >= 100;
    }

    public String getGameOverReason() {
        if (suspicionLevel >= MAX_SUSPICION)
            return "Le spie ti hanno scoperto!";
        if (daysRemaining <= 0)
            return "Il tempo e' scaduto!";
        return "";
    }

    /**
     * Risultato di una scelta di dialogo.
     */
    public static class DialogueResult {
        public final boolean success;
        public final String response;
        public final Clue discoveredClue;
        public final int experienceGained;

        public DialogueResult(boolean success, String response,
                              Clue discoveredClue, int experienceGained) {
            this.success = success;
            this.response = response;
            this.discoveredClue = discoveredClue;
            this.experienceGained = experienceGained;
        }
    }
}