package it.unicam.cs.mpgc.rpg130929.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Location {

    private final String id;
    private final String name;
    private final String description;
    private final List<Clue> clues;
    private boolean visited;

    public Location(String id, String name, String description) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Id non valido");
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Nome non valido");
        this.id = id;
        this.name = name;
        this.description = description;
        this.clues = new ArrayList<>();
        this.visited = false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isVisited() { return visited; }
    public void visit() { this.visited = true; }

    public void addClue(Clue clue) {
        if (clue == null) throw new IllegalArgumentException("Indizio non valido");
        if (!clues.contains(clue)) clues.add(clue);
    }

    public List<Clue> getClues() {
        return Collections.unmodifiableList(clues);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Location)) return false;
        Location other = (Location) obj;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}