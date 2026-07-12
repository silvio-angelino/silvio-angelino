package it.unicam.cs.mpgc.rpg130929.controller;

import it.unicam.cs.mpgc.rpg130929.repository.InMemoryGameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    private GameController controller;

    @BeforeEach
    void setUp() {
        controller = new GameController(new InMemoryGameRepository());
    }

    @Test
    void nuovaPartitaNonHaSalvataggi() {
        assertFalse(controller.hasSavedGame());
    }

    @Test
    void saveGameCreaUnSalvataggio() {
        controller.saveGame();
        assertTrue(controller.hasSavedGame());
    }

    @Test
    void spostarsiAumentaIlSospettoEAvanzaIlGiorno() {
        int sospettoIniziale = controller.getSuspicionLevel();
        int giorniIniziali = controller.getDaysRemaining();

        controller.moveToLocation("porto");

        assertTrue(controller.getSuspicionLevel() > sospettoIniziale);
        assertEquals(giorniIniziali - 1, controller.getDaysRemaining());
    }

    @Test
    void raccogliereProveLeContaCorrettamente() {
        controller.moveToLocation("porto");
        int trovate = controller.collectAllCluesInCurrentLocation();
        assertTrue(trovate >= 0);
    }

    @Test
    void spostarsiVersoLuogoInesistenteLanciaEccezione() {
        assertThrows(IllegalArgumentException.class,
                    () -> controller.moveToLocation("luogo_che_non_esiste"));
    }
}