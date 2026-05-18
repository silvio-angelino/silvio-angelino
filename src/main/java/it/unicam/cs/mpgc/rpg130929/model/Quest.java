package it.unicam.cs.mpgc.rpg130929.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rappresenta una missione del gioco.
 * Le missioni hanno obiettivi da completare e
 * ricompense in esperienza.
 *
 * @author Silvio Angelino
 * @version 1.0
 */
public class Quest {

    private final String id;
    private final String title;
    private final String description;
    private final List<String> objectives;
    private final List<Boolean> completedObjectives;
    private final int experienceReward;
    private boolean completed;

    public Quest() {
        this.id = null;
        this.title = null;
        this.description = null;
        this.objectives = new ArrayList<>();
        this.completedObjectives = new ArrayList<>();
        this.experienceReward = 0;
        this.completed = false;
    }

    public Quest(String id, String title, String description,
                 List<String> objectives, int experienceReward) {
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException("Id non valido");
        if (title == null || title.isEmpty())
            throw new IllegalArgumentException("Titolo non valido");
        this.id = id;
        this.title = title;
        this.description = description;
        this.objectives = new ArrayList<>(objectives);
        this.completedObjectives = new ArrayList<>();
        for (int i = 0; i < objectives.size(); i++) {
            this.completedObjectives.add(false);
        }
        this.experienceReward = experienceReward;
        this.completed = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getExperienceReward() { return experienceReward; }
    public boolean isCompleted() { return completed; }

    public List<String> getObjectives() {
        return Collections.unmodifiableList(objectives);
    }

    /**
     * Completa un obiettivo della missione.
     * Se tutti gli obiettivi sono completati,
     * la missione viene marcata come completata.
     *
     * @param index l'indice dell'obiettivo da completare
     */
    public void completeObjective(int index) {
        if (index >= 0 && index < completedObjectives.size()) {
            completedObjectives.set(index, true);
            checkCompletion();
        }
    }

    public boolean isObjectiveCompleted(int index) {
        if (index >= 0 && index < completedObjectives.size()) {
            return completedObjectives.get(index);
        }
        return false;
    }

    private void checkCompletion() {
        completed = completedObjectives.stream()
                .allMatch(Boolean::booleanValue);
    }

    public int getCompletedObjectivesCount() {
        return (int) completedObjectives.stream()
                .filter(Boolean::booleanValue)
                .count();
    }

    public int getTotalObjectivesCount() {
        return objectives.size();
    }
}
