package it.unicam.cs.mpgc.rpg130929.interfaces;

/**
 * Interfaccia funzionale per il calcolo della reputazione.
 * Essendo annotata con {@link FunctionalInterface}, può essere
 * implementata tramite espressioni lambda.
 * Questo permette di cambiare facilmente la strategia di calcolo
 * della reputazione senza modificare il codice esistente (OCP).
 *
 * @author Silvio Angelino
 * @version 1.0
 */
@FunctionalInterface
public interface ReputationCalculator {

    /**
     * Calcola la reputazione in base al numero di indizi usati.
     *
     * @param cluesCount il numero di indizi utilizzati
     * @return il valore della reputazione calcolata
     */
    int calculate(int cluesCount);
}