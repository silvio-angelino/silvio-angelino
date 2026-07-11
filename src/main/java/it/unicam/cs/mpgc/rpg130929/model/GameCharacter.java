package it.unicam.cs.mpgc.rpg130929.model;

// classe base per tutti i personaggi del gioco
// sia il giornalista che gli NPC estendono questa classe
public abstract class GameCharacter {

    private String name;
    private String role;

    // serve a Gson per la deserializzazione
    public GameCharacter() {}

    public GameCharacter(String name, String role) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Nome non valido");
        if (role == null || role.isEmpty())
            throw new IllegalArgumentException("Ruolo non valido");
        this.name = name;
        this.role = role;
    }

    public String getName() { return name; }

    public String getRole() { return role; }

    // ogni personaggio si presenta in modo diverso
    public abstract void introduceSelf();

    @Override
    public String toString() {
        return name + " - " + role;
    }
}