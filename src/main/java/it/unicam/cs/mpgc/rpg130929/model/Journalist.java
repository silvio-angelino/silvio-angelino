package it.unicam.cs.mpgc.rpg130929.model;

import java.util.ArrayList;
import java.util.List;

// il personaggio principale controllato dal giocatore
public class Journalist extends GameCharacter {

    private int reputation;
    private PlayerStats stats;
    private List<Clue> notebook;
    private List<Article> articles;
    private List<Location> visitedLocations;
    private List<Quest> activeQuests;
    private List<Quest> completedQuests;

    // serve a Gson
    public Journalist() {
        super();
        this.notebook = new ArrayList<>();
        this.articles = new ArrayList<>();
        this.visitedLocations = new ArrayList<>();
        this.activeQuests = new ArrayList<>();
        this.completedQuests = new ArrayList<>();
        this.stats = new PlayerStats();
    }

    public Journalist(String name) {
        super(name, "Agente Segreto");
        this.reputation = 0;
        this.stats = new PlayerStats();
        this.notebook = new ArrayList<>();
        this.articles = new ArrayList<>();
        this.visitedLocations = new ArrayList<>();
        this.activeQuests = new ArrayList<>();
        this.completedQuests = new ArrayList<>();
    }

    @Override
    public void introduceSelf() {
        System.out.println("Sono " + getName() +
                ", agente segreto sotto copertura.");
    }

    public int getReputation() { return reputation; }

    public PlayerStats getStats() { return stats; }

    // aggiunge un indizio al taccuino e guadagna XP
    public void addClueToNotebook(Clue clue) {
        if (clue == null)
            throw new IllegalArgumentException("Indizio non valido");
        if (!notebook.contains(clue)) {
            notebook.add(clue);
            clue.discover();
            stats.addExperience(20);
        }
    }

    public void addArticle(Article article) {
        if (article == null)
            throw new IllegalArgumentException("Articolo non valido");
        if (!articles.contains(article))
            articles.add(article);
    }

    public void publishArticle(Article article) {
        if (article == null)
            throw new IllegalArgumentException("Articolo non valido");
        article.publish();
        reputation += article.getReputationValue();
        stats.addExperience(50);
    }

    public void visitLocation(Location location) {
        if (location == null)
            throw new IllegalArgumentException("Luogo non valido");
        if (!visitedLocations.contains(location)) {
            visitedLocations.add(location);
            location.visit();
            stats.addExperience(10);
        }
    }

    public void addQuest(Quest quest) {
        if (quest == null)
            throw new IllegalArgumentException("Missione non valida");
        if (!activeQuests.contains(quest))
            activeQuests.add(quest);
    }

    // sposta la missione da attiva a completata
    public void completeQuest(Quest quest) {
        if (quest == null)
            throw new IllegalArgumentException("Missione non valida");
        if (activeQuests.contains(quest)) {
            activeQuests.remove(quest);
            completedQuests.add(quest);
            stats.addExperience(quest.getExperienceReward());
            reputation += quest.getExperienceReward() / 10;
        }
    }

    public List<Clue> getNotebook() { return notebook; }

    public List<Article> getArticles() { return articles; }

    public List<Location> getVisitedLocations() {
        return visitedLocations;
    }

    public List<Quest> getActiveQuests() { return activeQuests; }

    public List<Quest> getCompletedQuests() {
        return completedQuests;
    }
}