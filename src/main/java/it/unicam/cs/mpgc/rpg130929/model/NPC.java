package it.unicam.cs.mpgc.rpg130929.model;

import java.util.ArrayList;
import java.util.List;

// personaggio non giocante con cui il giornalista può parlare
public class NPC extends GameCharacter {

    private String id;
    private List<String> dialogues;
    private List<Clue> cluesProvided;

    // serve a Gson
    public NPC() {
        super();
        this.dialogues = new ArrayList<>();
        this.cluesProvided = new ArrayList<>();
    }

    public NPC(String id, String name, String role) {
        super(name, role);
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException("Id non valido");
        this.id = id;
        this.dialogues = new ArrayList<>();
        this.cluesProvided = new ArrayList<>();
    }

    @Override
    public void presentati() {
        System.out.println("Sono " + getName() +
                ", " + getRole() + ".");
    }

    public String getId() { return id; }

    public void addDialogue(String dialogue) {
        if (dialogue == null || dialogue.isEmpty()) return;
        dialogues.add(dialogue);
    }

    public void addClue(Clue clue) {
        if (clue == null) return;
        if (!cluesProvided.contains(clue))
            cluesProvided.add(clue);
    }

    public List<String> getDialogues() {
        return dialogues;
    }

    public List<Clue> getCluesProvided() {
        return cluesProvided;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NPC other)) return false;
        return this.id != null && this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}