package it.unicam.cs.mpgc.rpg130929.interfaces;

import it.unicam.cs.mpgc.rpg130929.model.GameState;

public interface GameRepository {

    void saveGame(GameState state);

    GameState loadGame();

    boolean hasSavedGame();
}
