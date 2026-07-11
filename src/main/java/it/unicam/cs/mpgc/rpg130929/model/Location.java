package it.unicam.cs.mpgc.rpg130929.model;

import it.unicam.cs.mpgc.rpg130929.interfaces.Describable;
import it.unicam.cs.mpgc.rpg130929.interfaces.Identifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// rappresenta un luogo della città che il giocatore può visitare
public class Location implements Identifiable, Describable {

    private String id;
    private String name;
    private String description;
    private List<Clue> clues;
    private boolean visited;

    // posizione sulla mappa, letta dal JSON (usata da MapView)
    private int x;
    private int y;

    // serve a Gson
    public Location() {
        this.clues = new ArrayList<>();
        this.visited = false;
    }

    public Location(String id, String name, String description) {
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException("Id non valido");
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Nome non valido");
        this.id = id;
        this.name = name;
        this.description = description;
        this.clues = new ArrayList<>();
        this.visited = false;
    }

    @Override
    public String getId() { return id; }

    public String getName() { return name; }

    @Override
    public String getDescription() { return description; }

    public boolean isVisited() { return visited; }

    public int getX() { return x; }

    public int getY() { return y; }

    public void visit() {
        this.visited = true;
    }

    public void addClue(Clue clue) {
        if (clue == null) return;
        if (!clues.contains(clue))
            clues.add(clue);
    }

    // copia difensiva: chi chiama non puo' modificare la lista
    // interna bypassando addClue()
    public List<Clue> getClues() {
        return Collections.unmodifiableList(clues);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Location other)) return false;
        return this.id != null && this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}