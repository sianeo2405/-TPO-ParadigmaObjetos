package model;

import controller.CombatEngine;

public class Mage extends PartyMember {

    public Mage(String name, int maxHp, int maxMp, int attack, int defense, int speed) {
        super(name, maxHp, maxMp, attack, defense, speed);
    }

    @Override public String getRoleName() { return "Mago"; }
    @Override public String getSkillName() { return "Explosión Arcana"; }
    @Override public String getSkillDescription() { return "Daña a todos los combatientes (enemigos y aliados) en el campo."; }
    @Override public int getSkillMpCost() { return 30; }

    @Override
    public void executeSkill(CombatEngine engine, PartyMember allyTarget, Enemy enemyTarget) {
        int damage = this.getAttack() * 3 + 10;
        
        // El mago le pega a todo lo que se mueva en el campo de batalla
        for (Combatant c : engine.getTurnOrder()) {
            if (c.isAlive()) {
                c.takeDamage(damage);
            }
        }
    }

    // Crecimiento de estadísticas al subir de nivel
    @Override protected int getHpGrowth() { return 10; }
    @Override protected int getMpGrowth() { return 20; }
    @Override protected int getAttackGrowth() { return 1; }
    @Override protected int getDefenseGrowth() { return 1; }
    @Override protected int getSpeedGrowth() { return 1; }
}