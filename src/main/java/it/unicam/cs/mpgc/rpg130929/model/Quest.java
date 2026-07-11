package it.unicam.cs.mpgc.rpg130929.model;

import it.unicam.cs.mpgc.rpg130929.interfaces.Identifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// rappresenta una missione con obiettivi da completare
public class Quest implements Identifiable {

    private String id;
    private String title;
    private String description;
    private List<String> objectives;
    private List<Boolean> completedObjectives;
    private int experienceReward;
    private boolean completed;

    // serve a Gson
    public Quest() {
        this.objectives = new ArrayList<>();
        this.completedObjectives = new ArrayList<>();
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
        this.experienceReward = experienceReward;
        this.completed = false;

        // inizializzo tutti gli obiettivi come non completati
        this.completedObjectives = new ArrayList<>();
        for (int i = 0; i < objectives.size(); i++) {
            completedObjectives.add(false);
        }
    }

    @Override
    public String getId() { return id; }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getExperienceReward() { return experienceReward; }
    public boolean isCompleted() { return completed; }

    // copia difensiva: chi chiama non puo' modificare la lista
    // interna degli obiettivi
    public List<String> getObjectives() {
        return Collections.unmodifiableList(objectives);
    }

    // segna un obiettivo come completato
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

    // controlla se tutti gli obiettivi sono completati
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Quest other)) return false;
        return this.id != null && this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}