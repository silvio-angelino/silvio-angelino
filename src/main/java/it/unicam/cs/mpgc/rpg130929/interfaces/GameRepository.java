package it.unicam.cs.mpgc.rpg130929.interfaces;

import it.unicam.cs.mpgc.rpg130929.model.GameState;

/**
 * Interfaccia che definisce il contratto per la persistenza
 * dello stato di gioco.
 * Seguendo il principio DIP (Dependency Inversion Principle),
 * il controller dipende da questa astrazione e non da una
 * implementazione concreta.
 * Implementazioni possibili: JSON, database, file binario, ecc.
 *
 * @author Silvio Angelino
 * @version 1.0
 */
public interface GameRepository {

    /**
     * Salva lo stato corrente del gioco.
     *
     * @param state lo stato del gioco da salvare
     */
    void saveGame(GameState state);

    /**
     * Carica lo stato del gioco precedentemente salvato.
     *
     * @return lo stato del gioco caricato
     */
    GameState loadGame();

    /**
     * Verifica se esiste un salvataggio precedente.
     *
     * @return true se esiste un salvataggio, false altrimenti
     */
    boolean hasSavedGame();
}