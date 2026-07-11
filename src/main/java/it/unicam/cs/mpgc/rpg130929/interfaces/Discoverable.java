package it.unicam.cs.mpgc.rpg130929.interfaces;

// interfaccia per gli oggetti che possono essere scoperti
public interface Discoverable {
    boolean isDiscovered();
    void discover();

    // metodo default: stato leggibile per log o debug,
    // riusa i due metodi astratti sopra senza che ogni
    // classe debba reimplementarlo
    default String discoveryStatus() {
        return isDiscovered() ? "scoperto" : "non scoperto";
    }
}