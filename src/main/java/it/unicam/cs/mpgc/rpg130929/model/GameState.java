package it.unicam.cs.mpgc.rpg130929.model;

// contiene lo stato del gioco da salvare su file
public class GameState {

    private Journalist journalist;
    private String currentLocationId;

    // serve a Gson
    public GameState() {}

    public GameState(Journalist journalist, String currentLocationId) {
        if (journalist == null)
            throw new IllegalArgumentException("Giornalista non valido");
        this.journalist = journalist;
        this.currentLocationId = currentLocationId;
    }

    public Journalist getJournalist() { return journalist; }

    public String getCurrentLocationId() { return currentLocationId; }
}