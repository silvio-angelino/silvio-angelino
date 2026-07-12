package it.unicam.cs.mpgc.rpg130929.controller;

import it.unicam.cs.mpgc.rpg130929.interfaces.GameRepository;
import it.unicam.cs.mpgc.rpg130929.interfaces.ReputationCalculator;
import it.unicam.cs.mpgc.rpg130929.model.*;
import it.unicam.cs.mpgc.rpg130929.repository.GameDataLoader;

import java.util.*;
import java.util.stream.Collectors;

// controller principale che gestisce tutta la logica di gioco
// dipende da GameRepository (interfaccia) e non da JsonGameRepository (DIP)
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

    // indice NPC-per-luogo costruito una sola volta al caricamento,
    // invece di rileggere il JSON grezzo ad ogni richiesta
    private final Map<String, List<NPC>> npcsByLocation;

    private Location currentLocation;

    // sistema sospetto e giorni
    private int suspicionLevel;
    private int daysRemaining;
    private static final int MAX_SUSPICION = 100;
    private static final int MAX_DAYS = 10;

    public GameController(GameRepository repository) {
        if (repository == null)
            throw new IllegalArgumentException("Repository non valido");
        this.repository = repository;
        this.dataLoader = new GameDataLoader();

        // uso una lambda per il calcolo della reputazione
        this.reputationCalculator = cluesCount -> cluesCount * 10;

        this.locations = new HashMap<>();
        this.npcs = new HashMap<>();
        this.clues = new HashMap<>();
        this.quests = new HashMap<>();
        this.npcChoices = new HashMap<>();
        this.npcsByLocation = new HashMap<>();
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

    // carico gli indizi dal JSON e li metto nella mappa
    private void loadClues() {
        dataLoader.loadClues().forEach(clue ->
                clues.put(clue.getId(), clue));
    }

    private void loadLocations() {
        dataLoader.loadLocations().forEach(location -> {
            // aggiungo gli indizi al luogo corrispondente
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
            // indicizzo l'NPC per luogo una sola volta, qui,
            // cosi' getNpcsInCurrentLocation() non deve piu'
            // rileggere il JSON grezzo ad ogni chiamata
            npcsByLocation
                    .computeIfAbsent(npcData.locationId, k -> new ArrayList<>())
                    .add(npc);
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

    // restituisce solo gli NPC presenti nel luogo corrente,
    // leggendo dall'indice gia' pronto invece di ricalcolarlo
    public List<NPC> getNpcsInCurrentLocation() {
        return Collections.unmodifiableList(
                npcsByLocation.getOrDefault(
                        currentLocation.getId(), Collections.emptyList()));
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
        // muoversi aumenta il sospetto e avanza il giorno
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

    // restituisce il numero di indizi effettivamente trovati
    public int collectAllCluesInCurrentLocation() {
        List<Clue> newlyCollected = currentLocation.getClues().stream()
                .filter(clue -> !clue.isDiscovered())
                .collect(Collectors.toList());
        newlyCollected.forEach(this::collectClue);
        increaseSuspicion(10);
        return newlyCollected.size();
    }

    public Clue getClueById(String clueId) {
        return clues.get(clueId);
    }

    // gestisce la scelta di dialogo e restituisce il risultato
    public DialogueResult processChoice(String npcId,
                                        GameDataLoader.ChoiceData choice) {
        boolean canUse = journalist.getStats()
                .canUseDialogueOption(choice.requiredCharisma);

        if (!canUse) {
            increaseSuspicion(5);
            return new DialogueResult(false,
                    "Il tuo carisma non e' sufficiente. " +
                            "L'NPC ti guarda con sospetto.",
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
        journalist.getActiveQuests().forEach(
                this::updateQuestProgress);
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
        // uso il timestamp come id univoco
        Article article = new Article(
                "a" + System.currentTimeMillis(), title);
        journalist.addArticle(article);
        return article;
    }

    public void publishArticle(Article article) {
        journalist.publishArticle(article);
        // pubblicare un articolo riduce il sospetto
        decreaseSuspicion(15);
        saveGame();
    }

    // crea un articolo con tutte le prove del taccuino e lo pubblica
    public Article writeArticleFromNotebook(String title) {
        Article article = createArticle(title);
        journalist.getNotebook().forEach(article::addClue);
        publishArticle(article);
        return article;
    }

    public void saveGame() {
        GameState state = new GameState(journalist,
                currentLocation.getId());
        repository.saveGame(state);
    }

    public boolean hasSavedGame() {
        return repository.hasSavedGame();
    }

    // carica una partita salvata, se presente; restituisce true se riuscito
    public boolean loadGame() {
        if (!repository.hasSavedGame()) return false;
        GameState state = repository.loadGame();
        if (state == null) return false;

        this.journalist = state.getJournalist();

        Location savedLocation = locations.get(state.getCurrentLocationId());
        this.currentLocation = savedLocation != null ?
                savedLocation : locations.get("redazione");

        // riallineo lo stato "visitato" dei luoghi con quanto salvato
        journalist.getVisitedLocationIds().forEach(id -> {
            Location location = locations.get(id);
            if (location != null) location.visit();
        });

        return true;
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
        suspicionLevel = Math.min(
                suspicionLevel + amount, MAX_SUSPICION);
    }

    public void decreaseSuspicion(int amount) {
        suspicionLevel = Math.max(
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

    // restituisce l'obiettivo corrente in base allo stato del gioco
    public String getCurrentObjective() {
        int clues = getDiscoveredCluesCount();
        int npcsContacted = (int) npcs.values().stream()
                .filter(npc -> npc.getCluesProvided().stream()
                        .anyMatch(Clue::isDiscovered))
                .count();
        int rep = journalist.getReputation();

        if (clues == 0) {
            return "OBIETTIVO:\nSpostati in un luogo\n" +
                    "e cerca prove!";
        } else if (clues < 3) {
            return "OBIETTIVO:\nRaccogli ancora prove!\n" +
                    "(" + clues + "/3 trovate)";
        } else if (npcsContacted < 2) {
            return "OBIETTIVO:\nParla con i contatti!\n" +
                    "(" + npcsContacted + "/2 contattati)";
        } else if (clues < 5) {
            return "OBIETTIVO:\nRaccogli altre prove!\n" +
                    "(" + clues + "/5 trovate)";
        } else if (rep == 0) {
            return "OBIETTIVO:\nHai abbastanza prove!\n" +
                    "Scrivi il rapporto finale!";
        } else if (rep < 100) {
            return "OBIETTIVO:\nContinua a raccogliere\n" +
                    "prove e pubblica rapporti!\n" +
                    "(" + rep + "/100 credibilita')";
        } else {
            return "MISSIONE COMPLETATA!";
        }
    }

    // value object per il risultato di una scelta di dialogo
    public record DialogueResult(boolean success, String response,
                                 Clue discoveredClue, int experienceGained) {}
}