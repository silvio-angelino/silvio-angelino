package it.unicam.cs.mpgc.rpg130929.model;

public class Clue {

    private final String id;
    private final String description;
    private final String locationId;
    private boolean discovered;

    public Clue(String id, String description, String locationId) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Id non valido");
        if (description == null || description.isEmpty()) throw new IllegalArgumentException("Descrizione non valida");
        this.id = id;
        this.description = description;
        this.locationId = locationId;
        this.discovered = false;
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public String getLocationId() { return locationId; }
    public boolean isDiscovered() { return discovered; }
    public void discover() { this.discovered = true; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Clue)) return false;
        Clue other = (Clue) obj;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}