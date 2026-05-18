package it.unicam.cs.mpgc.rpg130929.repository;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.unicam.cs.mpgc.rpg130929.model.*;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class GameDataLoader {

    private final Gson gson;

    public GameDataLoader() {
        this.gson = new Gson();
    }

    public List<Location> loadLocations() {
        return loadFromResources("locations.json",
                new TypeToken<List<Location>>(){}.getType());
    }

    public List<Clue> loadClues() {
        return loadFromResources("clues.json",
                new TypeToken<List<Clue>>(){}.getType());
    }

    public List<NpcData> loadNpcs() {
        return loadFromResources("npcs.json",
                new TypeToken<List<NpcData>>(){}.getType());
    }

    private <T> T loadFromResources(String filename, Type type) {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream(filename);
             InputStreamReader reader = new InputStreamReader(is)) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento di " + filename, e);
        }
    }

    public static class NpcData {
        public String id;
        public String name;
        public String role;
        public String locationId;
        public List<String> dialogues;
        public List<String> clueIds;
    }
}
