package model;

import java.util.ArrayList;
import java.util.List;

// Representa un miembro del grupo del jugador, con estadísticas y habilidades específicas según su clase.

public final class PartyMember extends Combatant {
    private final String name;
    private final CharacterClass characterClass;
    private int maxHp;
    private int currentHp;
    private int maxMp;
    private int currentMp;
    private int attack;
    private int defense;
    private int speed;
    private int attackBuff;
    private int defenseBuff;
    private int level = 1;
    private int xp = 0;

    private static final int[] XP_THRESHOLDS = {0, 100, 250, 450, 700, 1000};

    public PartyMember(String name, CharacterClass characterClass, int maxHp, int maxMp, int attack, int defense, int speed) {
        super(name, maxHp, attack, defense, speed);
        this.name = name;
        this.characterClass = characterClass;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.maxMp = maxMp;
        this.currentMp = maxMp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxMp() {
        return maxMp;
    }

    public int getCurrentMp() {
        return currentMp;
    }

    public int getAttack() {
        return attack + attackBuff;
    }

    public int getDefense() {
        return defense + defenseBuff;
    }

    public int getSpeed() {
        return speed;
    }

    public int getBaseAttack() {
        return attack;
    }

    public int getBaseDefense() {
        return defense;
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getXpToNextLevel() {
        if (level >= XP_THRESHOLDS.length) {
            return Integer.MAX_VALUE;
        }
        return XP_THRESHOLDS[level] - xp;
    }

    public String addXp(int amount) {
        if (level >= XP_THRESHOLDS.length) {
            return name + " ya ha alcanzado el nivel máximo.";
        }
        xp += amount;
        if (xp >= getXpToNextLevel()) {
            level++;
            int hpGain = switch (characterClass) {
                case WARRIOR -> 20;
                case MAGE -> 10;
                case ARCHER -> 15;
                case HEALER -> 20;
                default -> 0;
            };
            int mpGain = switch (characterClass) {
                case WARRIOR -> 5;
                case MAGE -> 20;
                case ARCHER -> 10;
                case HEALER -> 15;
                default -> 0;
            };
            int attackGain = switch (characterClass) {
                case WARRIOR -> 3;
                case MAGE -> 1;
                case ARCHER -> 3;
                case HEALER -> 1;
                default -> 0;
            };
            int defenseGain = switch (characterClass) {
                case WARRIOR -> 3;
                case MAGE -> 1;
                case ARCHER -> 1;
                case HEALER -> 2;
                default -> 0;
            };
            int speedGain = switch (characterClass) {
                case WARRIOR -> 1;
                case MAGE -> 1;
                case ARCHER -> 2;
                case HEALER -> 1;
                default -> 0;
            };
            maxHp += hpGain;
            currentHp = Math.min(currentHp + hpGain, maxHp);
            currentMp = Math.min(currentMp + mpGain, maxMp);
            attack += attackGain;
            defense += defenseGain;
            speed += speedGain;
            return "¡" + name + " ha subido al nivel " + level + "!";
        }
        return null;
    }

    public void takeDamage(int amount) {
        int reduced = Math.max(1, amount - getDefense());
        currentHp = Math.max(0, currentHp - reduced);
    }

    public void heal(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    public boolean spendMp(int amount) {
        if (currentMp < amount) {
            return false;
        }
        currentMp -= amount;
        return true;
    }

    public int restoreMp(int amount) {
        int restoredMp = Math.min(maxMp - currentMp, amount);
        currentMp = Math.min(maxMp, currentMp + amount);
        return restoredMp;
    }

    public void fullRestore() {
        currentHp = maxHp;
        currentMp = maxMp;
        attackBuff = 0;
        defenseBuff = 0;
    }

    public void boostAttack(int amount) {
        attack += amount;
    }

    public void boostDefense(int amount) {
        defense += amount;
    }

    public void applyAttackBuff(int amount) {
        attackBuff += amount;
    }

    public void applyDefenseBuff(int amount) {
        defenseBuff += amount;
    }

    public int revive(int hp) {
        int restoredHp = maxHp / 2 + hp;
        currentHp = restoredHp;
        return restoredHp;
    }

    public void clearBuffs() {
        attackBuff = 0;
        defenseBuff = 0;
    }

    public int getSkillMpCost() {
        return switch (characterClass) {
            case WARRIOR -> 8;
            case MAGE -> 30;
            case ARCHER -> 20;
            case HEALER -> 12;
        };
    }

    @Override
    public String toString() {
        return name + " (" + currentHp + "/" + maxHp + " HP)";
    }

    public static Party createDefaultParty() {
        List<PartyMember> members = new ArrayList<>();
        members.add(new PartyMember("Altria", CharacterClass.WARRIOR, 120, 30, 18, 8, 10));
        members.add(new PartyMember("Jean", CharacterClass.HEALER, 85, 50, 12, 3, 12));
        members.add(new PartyMember("Emil", CharacterClass.ARCHER, 70, 35, 20, 4, 15));
        members.add(new PartyMember("Mesh", CharacterClass.MAGE, 60, 70, 10, 2, 14));
        return new Party(members);
    }
}
