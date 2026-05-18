package it.unicam.cs.mpgc.rpg130929.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NPC extends GameCharacter {

    private String id;
    private List<String> dialogues;
    private List<Clue> cluesProvided;

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
        System.out.println("Sono " + getName() + ", " + getRole() + ".");
    }

    public String getId() { return id; }

    public void addDialogue(String dialogue) {
        if (dialogue == null || dialogue.isEmpty())
            throw new IllegalArgumentException("Dialogo non valido");
        if (dialogues == null) dialogues = new ArrayList<>();
        dialogues.add(dialogue);
    }

    public void addClue(Clue clue) {
        if (clue == null) return;
        if (cluesProvided == null) cluesProvided = new ArrayList<>();
        if (!cluesProvided.contains(clue)) cluesProvided.add(clue);
    }

    public List<String> getDialogues() {
        if (dialogues == null) dialogues = new ArrayList<>();
        return Collections.unmodifiableList(dialogues);
    }

    public List<Clue> getCluesProvided() {
        if (cluesProvided == null) cluesProvided = new ArrayList<>();
        return Collections.unmodifiableList(cluesProvided);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NPC)) return false;
        NPC other = (NPC) obj;
        return this.id != null && this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}