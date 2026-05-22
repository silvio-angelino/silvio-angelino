package it.unicam.cs.mpgc.rpg130929.model;

// rappresenta una delle scelte disponibili nel dialogo con un NPC
// alcune scelte richiedono un carisma minimo per essere usate
public class DialogueChoice {

    private String text;
    private String response;
    private int requiredCharisma;
    private String clueId;
    private int experienceReward;

    // costruttore vuoto per Gson
    public DialogueChoice() {
        this.text = "";
        this.response = "";
        this.requiredCharisma = 0;
        this.clueId = null;
        this.experienceReward = 0;
    }

    public DialogueChoice(String text, String response,
                          int requiredCharisma, String clueId,
                          int experienceReward) {
        if (text == null || text.isEmpty())
            throw new IllegalArgumentException("Testo non valido");
        this.text = text;
        this.response = response;
        this.requiredCharisma = requiredCharisma;
        this.clueId = clueId;
        this.experienceReward = experienceReward;
    }

    public String getText() { return text; }

    public String getResponse() { return response; }

    public int getRequiredCharisma() { return requiredCharisma; }

    public String getClueId() { return clueId; }

    public int getExperienceReward() { return experienceReward; }

    // controlla se questa scelta fornisce un indizio
    public boolean hasClue() {
        return clueId != null && !clueId.isEmpty();
    }
}