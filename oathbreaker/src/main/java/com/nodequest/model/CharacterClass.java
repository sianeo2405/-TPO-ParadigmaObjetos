package com.nodequest.model;

public enum CharacterClass {
    WARRIOR("Guerrera", "Tacleada"),
    MAGE("Mago", "Explosión"),
    ARCHER("Arquero", "Tiro Certero"),
    HEALER("Curandera", "Curar");

    private final String displayName;
    private final String skillName;

    CharacterClass(String displayName, String skillName) {
        this.displayName = displayName;
        this.skillName = skillName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSkillName() {
        return skillName;
    }
}
