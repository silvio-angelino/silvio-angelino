package it.unicam.cs.mpgc.rpg130929.view;

import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.Map;

// registro singleton per il font pixel art: lo carica una sola volta
// dalle risorse e riusa la stessa famiglia per ottenere qualsiasi
// dimensione richiesta, invece di rileggere il file ad ogni view
public final class FontRegistry {

    private static final String FONT_PATH = "PressStart2P-Regular.ttf";
    private static FontRegistry instance;

    private final String fontFamily;
    private final Map<Double, Font> cache = new HashMap<>();

    private FontRegistry() {
        Font loaded = Font.loadFont(
                getClass().getClassLoader()
                        .getResourceAsStream(FONT_PATH), 10);
        this.fontFamily = loaded != null ? loaded.getFamily() : null;
    }

    public static synchronized FontRegistry getInstance() {
        if (instance == null) {
            instance = new FontRegistry();
        }
        return instance;
    }

    // restituisce il font pixel art alla dimensione richiesta;
    // null se il file non e' stato trovato in resources
    public Font get(double size) {
        if (fontFamily == null) return null;
        return cache.computeIfAbsent(size,
                s -> Font.font(fontFamily, s));
    }
}