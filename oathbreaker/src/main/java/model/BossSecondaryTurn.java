package model;

import controller.CombatEngine;

public class BossSecondaryTurn extends Enemy {
    private final Boss boss;

    public BossSecondaryTurn(Boss boss, int speed) {
        super(boss.getName(), boss.getMaxHp(), boss.getAttack(), boss.getDefense(), speed, 0, 0);
        this.boss = boss;
    }

    @Override
    public boolean isAlive() {
        return boss.isAlive();
    }

    @Override
    public int getCurrentHp() {
        return boss.getCurrentHp();
    }

    @Override
    public int getMaxHp() {
        return boss.getMaxHp();
    }

    @Override
    public int getAttack() {
        return boss.getAttack();
    }

    @Override
    public int getDefense() {
        return boss.getDefense();
    }

    @Override
    public void takeDamage(int amount) {
        boss.takeDamage(amount);
    }

    @Override
    public void takePoisonDamage(int amount) {
        boss.takePoisonDamage(amount);
    }

    @Override
    public void takeBleedDamage(int amount) {
        boss.takeBleedDamage(amount);
    }

    @Override
    public void executeTurn(CombatEngine engine, Party party) {
        boss.executeTurn(engine, party);
    }
}
