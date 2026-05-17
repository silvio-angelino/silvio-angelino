package it.unicam.cs.mpgc.rpg130929.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NPC {

    private final String id;
    private final String name;
    private final String role;
    private final List<String> dialogues;
    private final List<Clue> cluesProvided;

    public NPC(String id, String name, String role) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Id non valido");
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Nome non valido");
        if (role == null || role.isEmpty()) throw new IllegalArgumentException("Ruolo non valido");
        this.id = id;
        this.name = name;
        this.role = role;
        this.dialogues = new ArrayList<>();
        this.cluesProvided = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }

    public void addDialogue(String dialogue) {
        if (dialogue == null || dialogue.isEmpty()) throw new IllegalArgumentException("Dialogo non valido");
        dialogues.add(dialogue);
    }

    public void addClue(Clue clue) {
        if (clue == null) throw new IllegalArgumentException("Indizio non valido");
        if (!cluesProvided.contains(clue)) cluesProvided.add(clue);
    }

    public List<String> getDialogues() {
        return Collections.unmodifiableList(dialogues);
    }

    public List<Clue> getCluesProvided() {
        return Collections.unmodifiableList(cluesProvided);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NPC)) return false;
        NPC other = (NPC) obj;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}