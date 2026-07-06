package model;

// Representa un enemigo en el juego, con estadísticas de combate y recompensas por derrotarlo.

public final class Enemy extends Combatant {
    private int attackDebuff;
    private final int goldReward;
    private final int xpReward;

    public Enemy(String name, int maxHp, int attack, int defense, int speed, int goldReward, int xpReward) {
        super(name, maxHp, attack, defense, speed);
        this.goldReward = goldReward;
        this.xpReward = xpReward;
    }

    public int getGoldReward() {
        return goldReward;
    }

    public int getXpReward() {
        return xpReward;
    }

    @Override
    public int getAttack() {
        return Math.max(1, attack - attackDebuff);
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
        return new Enemy("Señor Dracónico", 750, 50, 10, 15, 0, 0);
    }

    @Override
    public String toString() {
        return name + " (" + currentHp + "/" + maxHp + " HP)";
    }
}
