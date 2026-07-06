package model;

// Representa los diferentes tipos de nodos en el mapa del juego, cada uno con un icono y una etiqueta descriptiva.

public enum NodeType {
    START("Inicio", "S"),
    COMBAT("Combate", "⚔"),
    ELITE("Élite", "☠"),
    REST("Descanso", "🔥"),
    TREASURE("Tesoro", "💎"),
    BOSS("JEFE", "👑"),
    SHOP("Tienda", "🛒");

    private final String label;
    private final String icon;

    NodeType(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }
}
