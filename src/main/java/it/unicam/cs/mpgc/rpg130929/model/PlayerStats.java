package it.unicam.cs.mpgc.rpg130929.model;

// statistiche RPG del personaggio
// queste influenzano cosa puoi fare nel gioco
public class PlayerStats {

    private int intelligence;
    private int charisma;
    private int stealth;
    private int level;
    private int experience;

    // ogni livello richiede 100 XP
    private static final int XP_PER_LEVEL = 100;

    public PlayerStats() {
        this.intelligence = 5;
        this.charisma = 5;
        this.stealth = 5;
        this.level = 1;
        this.experience = 0;
    }

    public int getIntelligence() { return intelligence; }
    public int getCharisma() { return charisma; }
    public int getStealth() { return stealth; }
    public int getLevel() { return level; }
    public int getExperience() { return experience; }

    public int getXpToNextLevel() {
        return (level * XP_PER_LEVEL) - experience;
    }

    // aggiunge XP e controlla se si sale di livello
    public void addExperience(int amount) {
        if (amount <= 0) return;
        this.experience += amount;
        while (experience >= level * XP_PER_LEVEL) {
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        intelligence++;
        charisma++;
        stealth++;
        System.out.println("Livello aumentato! Ora sei LV." + level);
    }

    // usato per controllare se puoi accedere a certi luoghi
    public boolean canAccess(int requiredStealth) {
        return stealth >= requiredStealth;
    }

    // usato per controllare le opzioni di dialogo disponibili
    public boolean canUseDialogueOption(int requiredCharisma) {
        return charisma >= requiredCharisma;
    }

    @Override
    public String toString() {
        return "LV." + level + " | INT:" + intelligence +
                " | CAR:" + charisma + " | FUR:" + stealth +
                " | XP:" + experience + "/" + (level * XP_PER_LEVEL);
    }
}