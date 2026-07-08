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

    protected int poisonTurns = 0;
    protected int poisonDamage = 0;
    protected int bleedTurns = 0;
    protected double bleedPercent = 0.0;

    public void applyPoison(int turns, int damagePerTurn) {
        this.poisonTurns = turns;
        this.poisonDamage = damagePerTurn;
    }

    public boolean isPoisoned() {
        return this.poisonTurns > 0;
    }

    public int getPoisonTurns() {
        return this.poisonTurns;
    }

    public int getPoisonDamage() {
        return this.poisonDamage;
    }

    public void applyBleed(int turns, double percent) {
        this.bleedTurns = turns;
        this.bleedPercent = percent;
    }

    public boolean isBleeding() {
        return this.bleedTurns > 0;
    }

    public int getBleedTurns() {
        return this.bleedTurns;
    }

    public double getBleedPercent() {
        return this.bleedPercent;
    }

    public void decrementStatusTurns() {
        if (this.poisonTurns > 0) this.poisonTurns--;
        if (this.bleedTurns > 0) this.bleedTurns--;
    }

    public void takePoisonDamage(int amount) {
        this.currentHp = Math.max(0, this.currentHp - amount);
    }

    public void takeBleedDamage(int amount) {
        this.currentHp = Math.max(0, this.currentHp - amount);
    }

    public void takeDamageIgnoringDefense(int amount) {
        this.currentHp = Math.max(0, this.currentHp - amount);
    }

    public void clearPoison() {
        this.poisonTurns = 0;
        this.poisonDamage = 0;
    }

    public void clearBleed() {
        this.bleedTurns = 0;
        this.bleedPercent = 0.0;
    }
}
