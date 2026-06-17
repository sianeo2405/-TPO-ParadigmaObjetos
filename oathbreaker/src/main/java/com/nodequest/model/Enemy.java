package com.nodequest.model;

public final class Enemy implements Combatant {
    private final String name;
    private final int maxHp;
    private int currentHp;
    private int attack;
    private int defense;
    private final int speed;
    private int attackDebuff;
    private final int goldReward;
    private final int xpReward;

    public Enemy(String name, int maxHp, int attack, int defense, int speed, int goldReward, int xpReward) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.goldReward = goldReward;
        this.xpReward = xpReward;
    }

    public String getName() {
        return name;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getGoldReward() {
        return goldReward;
    }

    public int getXpReward() {
        return xpReward;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getAttack() {
        return Math.max(1, attack - attackDebuff);
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public void takeDamage(int amount) {
        int reduced = Math.max(1, amount - defense);
        currentHp = Math.max(0, currentHp - reduced);
    }

    public void reduceAttack(int amount) {
        attackDebuff += amount;
    }

    public static Enemy goblin() {
        return new Enemy("Goblin", 45, 10, 2, 14, 15, 20);
    }

    public static Enemy wolf() {
        return new Enemy("Lobo", 55, 14, 3, 16, 20, 25);
    }

    public static Enemy skeleton() {
        return new Enemy("Esqueleto", 60, 12, 5, 9, 25, 30);
    }

    public static Enemy orc() {
        return new Enemy("Orco", 100, 18, 6, 11, 30, 35);
    }

    public static Enemy darkMage() {
        return new Enemy("Mago Oscuro", 80, 22, 4, 13, 25, 30);
    }

    public static Enemy boss() {
        return new Enemy("Señor Dracónico", 750, 65, 10, 15, 0, 0);
    }

    @Override
    public String toString() {
        return name + " (" + currentHp + "/" + maxHp + " HP)";
    }
}
