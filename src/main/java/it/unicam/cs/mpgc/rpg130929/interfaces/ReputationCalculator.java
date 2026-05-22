package it.unicam.cs.mpgc.rpg130929.interfaces;

// interfaccia funzionale per calcolare la reputazione
// essendo @FunctionalInterface si può usare con le lambda
// es: ReputationCalculator calc = clues -> clues * 10;
@FunctionalInterface
public interface ReputationCalculator {
    int calculate(int cluesCount);
}