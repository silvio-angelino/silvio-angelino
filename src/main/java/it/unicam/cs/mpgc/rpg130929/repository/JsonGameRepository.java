package it.unicam.cs.mpgc.rpg130929.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unicam.cs.mpgc.rpg130929.interfaces.GameRepository;
import it.unicam.cs.mpgc.rpg130929.model.GameState;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

// salva e carica lo stato del gioco su file JSON
public class JsonGameRepository implements GameRepository {

    private static final String SAVE_FILE = "savegame.json";
    private final Gson gson;

    public JsonGameRepository() {
        // uso prettyPrinting per rendere il file leggibile
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void saveGame(GameState state) {
        try (Writer writer = new FileWriter(SAVE_FILE)) {
            gson.toJson(state, writer);
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio: " + e.getMessage());
            throw new RuntimeException("Errore nel salvataggio del gioco", e);
        }
    }

    @Override
    public GameState loadGame() {
        try (Reader reader = new FileReader(SAVE_FILE)) {
            return gson.fromJson(reader, GameState.class);
        } catch (IOException e) {
            System.err.println("Errore nel caricamento: " + e.getMessage());
            throw new RuntimeException("Errore nel caricamento del gioco", e);
        }
    }

    @Override
    public boolean hasSavedGame() {
        return Files.exists(Paths.get(SAVE_FILE));
    }
}