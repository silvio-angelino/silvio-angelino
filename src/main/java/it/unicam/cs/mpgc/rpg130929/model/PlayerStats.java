package it.unicam.cs.mpgc.rpg130929.model;

/**
 * Rappresenta le statistiche del personaggio giocante.
 * Le statistiche influenzano le meccaniche di gioco:
 * - Intelligenza: influenza le prove trovate
 * - Carisma: influenza i dialoghi con gli NPC
 * - Furtivita': influenza l'accesso ai luoghi segreti
 *
 * @author Silvio Angelino
 * @version 1.0
 */
public class PlayerStats {

    private int intelligence;
    private int charisma;
    private int stealth;
    private int level;
    private int experience;
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

    /**
     * Aggiunge esperienza al personaggio.
     * Se l'esperienza raggiunge il limite, il personaggio
     * sale di livello e le statistiche aumentano.
     *
     * @param amount la quantità di esperienza da aggiungere
     */
    public void addExperience(int amount) {
        if (amount <= 0) return;
        this.experience += amount;
        while (experience >= level * XP_PER_LEVEL) {
            levelUp();
        }
    }

    private void levelUp() {
        this.level++;
        this.intelligence++;
        this.charisma++;
        this.stealth++;
    }

    /**
     * Verifica se il personaggio puo' accedere
     * a un luogo segreto in base alla furtivita'.
     *
     * @param requiredStealth la furtivita' richiesta
     * @return true se il personaggio puo' accedere
     */
    public boolean canAccess(int requiredStealth) {
        return this.stealth >= requiredStealth;
    }

    /**
     * Verifica se il personaggio puo' usare
     * una opzione di dialogo in base al carisma.
     *
     * @param requiredCharisma il carisma richiesto
     * @return true se il personaggio puo' usare l'opzione
     */
    public boolean canUseDialogueOption(int requiredCharisma) {
        return this.charisma >= requiredCharisma;
    }

    @Override
    public String toString() {
        return String.format(
                "LV.%d | INT:%d | CAR:%d | FUR:%d | XP:%d/%d",
                level, intelligence, charisma, stealth,
                experience, level * XP_PER_LEVEL);
    }
}
