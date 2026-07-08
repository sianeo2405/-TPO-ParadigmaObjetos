package model;

import controller.CombatEngine;

public class Warrior extends PartyMember {

    public Warrior(String name, int maxHp, int maxMp, int attack, int defense, int speed) {
        super(name, maxHp, maxMp, attack, defense, speed);
    }

    @Override public String getRoleName() { return "Guerrero"; }
    @Override public String getSkillName() { return "Tacleada Rompe-Armaduras"; }
    @Override public String getSkillDescription() { return "Hace daño extra y reduce el ataque del enemigo."; }
    @Override public int getSkillMpCost() { return 8; }

    @Override
    public void executeSkill(CombatEngine engine, PartyMember allyTarget, Enemy enemyTarget) {
        if (enemyTarget != null && enemyTarget.isAlive()) {
            int damage = this.getAttack() + 12;
            enemyTarget.takeDamage(damage);
            enemyTarget.reduceAttack(4);
        } else {
            // Si el objetivo ya murió o es nulo, le devolvemos el MP gastado
            this.restoreMp(getSkillMpCost()); 
        }
    }

    // Crecimiento de estadísticas al subir de nivel
    @Override protected int getHpGrowth() { return 20; }
    @Override protected int getMpGrowth() { return 5; }
    @Override protected int getAttackGrowth() { return 3; }
    @Override protected int getDefenseGrowth() { return 3; }
    @Override protected int getSpeedGrowth() { return 1; }
}
