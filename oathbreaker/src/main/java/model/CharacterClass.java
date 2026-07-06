package model;

// Representa las diferentes clases de personajes disponibles para los miembros del grupo del jugador, 
// y los nombres y descripciones de sus habilidades.

public enum CharacterClass {
    WARRIOR("Guerrera", "Tacleada", "ATK + 12 de daño, ATK - 4 al objetivo."),
    MAGE("Mago", "Explosión", "ATK * 3 + 10 a TODOS."),
    ARCHER("Arquero", "Tiro Certero","ATK * 5 + 5 a un objetivo."),
    HEALER("Curandera", "Curar", "MaxHP / 2 de curación al aliado objetivo.");

    private final String displayName;
    private final String skillName;
    private final String skillDescription;

    CharacterClass(String displayName, String skillName, String skillDescription) {
        this.displayName = displayName;
        this.skillName = skillName;
        this.skillDescription = skillDescription;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSkillName() {
        return skillName;
    }

    public String getSkillDescription(){
        return skillDescription;
    }
}
