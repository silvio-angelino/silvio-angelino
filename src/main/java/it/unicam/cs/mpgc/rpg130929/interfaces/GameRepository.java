package it.unicam.cs.mpgc.rpg130929.interfaces;

import it.unicam.cs.mpgc.rpg130929.model.GameState;

// interfaccia per la persistenza del gioco
// seguendo DIP, il controller dipende da questa e non da JsonGameRepository
// in futuro si potrebbe implementare anche con un database
public interface GameRepository {

    void saveGame(GameState state);

    GameState loadGame();

    boolean hasSavedGame();
}