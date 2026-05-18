package it.unicam.cs.mpgc.rpg130929.model;

import it.unicam.cs.mpgc.rpg130929.interfaces.Describable;
import it.unicam.cs.mpgc.rpg130929.interfaces.Discoverable;
import it.unicam.cs.mpgc.rpg130929.interfaces.Identifiable;

public class Clue implements Identifiable, Describable, Discoverable {

    private String id;
    private String description;
    private String locationId;
    private boolean discovered;

    public Clue() {
        this.discovered = false;
    }

    public Clue(String id, String description, String locationId) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Id non valido");
        if (description == null || description.isEmpty()) throw new IllegalArgumentException("Descrizione non valida");
        this.id = id;
        this.description = description;
        this.locationId = locationId;
        this.discovered = false;
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getDescription() { return description; }

    @Override
    public boolean isDiscovered() { return discovered; }

    @Override
    public void discover() { this.discovered = true; }

    public String getLocationId() { return locationId; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Clue)) return false;
        Clue other = (Clue) obj;
        return this.id != null && this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}