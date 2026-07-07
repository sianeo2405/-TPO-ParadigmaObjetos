package model;

import controller.CombatEngine;

public class Archer extends PartyMember {

    public Archer(String name, int maxHp, int maxMp, int attack, int defense, int speed) {
        super(name, maxHp, maxMp, attack, defense, speed);
    }

    @Override public String getRoleName() { return "Arquero"; }
    @Override public String getSkillName() { return "Tiro Certero"; }
    @Override public String getSkillDescription() { return "Causa daño masivo a un solo objetivo."; }
    @Override public int getSkillMpCost() { return 20; }

    @Override
    public void executeSkill(CombatEngine engine, PartyMember allyTarget, Enemy enemyTarget) {
        if (enemyTarget != null && enemyTarget.isAlive()) {
            int damage = this.getAttack() * 5 + 5;
            enemyTarget.takeDamage(damage);
        } else {
            this.restoreMp(getSkillMpCost());
        }
    }

    // Crecimiento de estadísticas al subir de nivel
    @Override protected int getHpGrowth() { return 15; }
    @Override protected int getMpGrowth() { return 10; }
    @Override protected int getAttackGrowth() { return 3; }
    @Override protected int getDefenseGrowth() { return 1; }
    @Override protected int getSpeedGrowth() { return 2; }
}