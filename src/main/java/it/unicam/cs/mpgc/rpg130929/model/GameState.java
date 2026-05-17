package it.unicam.cs.mpgc.rpg130929.model;

public class GameState {

    private final Journalist journalist;
    private String currentLocationId;

    public GameState(Journalist journalist, String currentLocationId) {
        if (journalist == null) throw new IllegalArgumentException("Giornalista non valido");
        this.journalist = journalist;
        this.currentLocationId = currentLocationId;
    }

    public Journalist getJournalist() { return journalist; }
    public String getCurrentLocationId() { return currentLocationId; }

    public void setCurrentLocationId(String locationId) {
        if (locationId == null || locationId.isEmpty()) throw new IllegalArgumentException("Location non valida");
        this.currentLocationId = locationId;
    }
}