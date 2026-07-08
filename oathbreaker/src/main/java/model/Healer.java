package model;

import controller.CombatEngine;

public class Healer extends PartyMember {

    public Healer(String name, int maxHp, int maxMp, int attack, int defense, int speed) {
        super(name, maxHp, maxMp, attack, defense, speed);
    }

    @Override public String getRoleName() { return "Curandera"; }
    @Override public String getSkillName() { return "Luz Sagrada"; }
    @Override public String getSkillDescription() { return "Cura gran cantidad de HP a un aliado."; }
    @Override public int getSkillMpCost() { return 12; }

    @Override
    public void executeSkill(CombatEngine engine, PartyMember allyTarget, Enemy enemyTarget) {
        // Si no hay aliado seleccionado o está muerto, se cura a sí misma
        PartyMember target = (allyTarget != null && allyTarget.isAlive()) ? allyTarget : this;
        int healAmount = this.getMaxHp() / 2;
        target.heal(healAmount);
        target.clearPoison();
        target.clearBleed();
    }

    // Crecimiento de estadísticas al subir de nivel
    @Override protected int getHpGrowth() { return 20; }
    @Override protected int getMpGrowth() { return 15; }
    @Override protected int getAttackGrowth() { return 1; }
    @Override protected int getDefenseGrowth() { return 2; }
    @Override protected int getSpeedGrowth() { return 1; }
}