package model;

// Abstracción para representar a cualquier combatiente en el juego, ya sea un miembro del grupo del jugador 
// o un enemigo, con estadísticas de combate básicas y métodos para gestionar su estado de salud.

public abstract class Combatant implements java.io.Serializable {
    protected final String name;
    protected int maxHp;
    protected int currentHp;
    protected int attack;
    protected int defense;
    protected int speed;

    public Combatant(String name, int maxHp, int attack, int defense, int speed) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public abstract boolean isPlayerControlled();

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public void takeDamage(int amount) {
        int reduced = Math.max(1, amount - getDefense());
        currentHp = Math.max(0, currentHp - reduced);
    }
}
