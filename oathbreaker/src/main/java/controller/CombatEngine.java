package controller;

import model.Combatant;
import model.Enemy;
import model.Party;
import model.PartyMember;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Motor de combate que gestiona los encuentros entre el grupo del jugador y los enemigos, 
// incluyendo el orden de turnos, las acciones de ataque y habilidades, y el registro de eventos del combate.

public final class CombatEngine implements java.io.Serializable {
    private final Party party;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<String> log = new ArrayList<>();
    private final Random random = new Random();
    private final List<Combatant> turnOrder = new ArrayList<>();
    private int currentCombatantIndex = 0;
    private boolean playerTurn = true;

    public CombatEngine(Party party) {
        this.party = party;
    }

    public void startEncounter(List<Enemy> encounterEnemies) {
        enemies.clear();
        enemies.addAll(encounterEnemies);
        log.clear();
        
        for (PartyMember member : party.getMembers()) {
            member.clearPoison();
            member.clearBleed();
        }

        determineTurnOrder();
        startNextTurn();
    }

    public Combatant getActiveCombatant() {
        if (currentCombatantIndex >= 0 && currentCombatantIndex < turnOrder.size()) {
            return turnOrder.get(currentCombatantIndex);
        }
        return null;
    }

    public PartyMember getActiveHero() {
        Combatant active = getActiveCombatant();
        if (active != null && active.isPlayerControlled()) {
            return (PartyMember) active;
        }
        return null;
    }

    private void determineTurnOrder() {
        turnOrder.clear();
        turnOrder.addAll(party.getAliveMembers());
        
        for (Enemy enemy : getAliveEnemies()) {
            if (enemy instanceof model.Boss boss) {
                turnOrder.add(boss);
                turnOrder.add(new model.BossSecondaryTurn(boss, boss.getSecondarySpeed()));
            } else if (!(enemy instanceof model.BossSecondaryTurn)) {
                turnOrder.add(enemy);
            }
        }
        
        turnOrder.sort((c1, c2) -> Integer.compare(c2.getSpeed(), c1.getSpeed()));
        currentCombatantIndex = 0;

        StringBuilder sb = new StringBuilder("Orden de turnos: ");
        for (int i = 0; i < turnOrder.size(); i++) {
            sb.append(turnOrder.get(i).getName());
            if (i < turnOrder.size() - 1) {
                sb.append(" -> ");
            }
        }
    }

    private void startNextTurn() {
        if (isCombatOver() || isPartyWiped()) {
            return;
        }

        while (currentCombatantIndex < turnOrder.size()) {
            Combatant next = turnOrder.get(currentCombatantIndex);
            
            if (!next.isAlive()) {
                currentCombatantIndex++;
                continue;
            }

            if (next.isPoisoned() || next.isBleeding()) {
                if (next.isPoisoned()) {
                    int pDmg = next.getPoisonDamage();
                    next.takePoisonDamage(pDmg);
                }
                if (next.isBleeding()) {
                    int bDmg = (int) (next.getMaxHp() * next.getBleedPercent());
                    bDmg = Math.max(1, bDmg);
                    next.takeBleedDamage(bDmg);
                }
                next.decrementStatusTurns();

                if (!next.isAlive()) {
                    if (isCombatOver() || isPartyWiped()) {
                        return;
                    }
                    currentCombatantIndex++;
                    continue;
                }
            }

            if (next.isPlayerControlled()) {
                playerTurn = true;
                return;
            } else {
                playerTurn = false;
                runSingleEnemyTurn((Enemy) next);
                currentCombatantIndex++;
                startNextTurn();
                return;
            }
        }

        determineTurnOrder();
        startNextTurn();
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Enemy> getAliveEnemies() {
        List<Enemy> alive = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                alive.add(enemy);
            }
        }
        return alive;
    }

    public List<String> getLog() {
        return List.copyOf(log);
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public boolean isCombatOver() {
        return getAliveEnemies().isEmpty();
    }

    public boolean isPartyWiped() {
        return party.isWiped();
    }

    public void attack(PartyMember attacker, Enemy target) {
        if (!playerTurn || !attacker.isAlive() || !target.isAlive()) {
            return;
        }
        int damage = attacker.getAttack() + random.nextInt(5);
        target.takeDamage(damage);
        attacker.restoreMp(attacker.getMaxMp()/10);
        endPlayerAction();
    }

public void useSkill(PartyMember caster, PartyMember allyTarget, Enemy enemyTarget) {
        if (!playerTurn || !caster.isAlive()) {
            return;
        }
        int mpCost = caster.getSkillMpCost(); // Ahora usa polimorfismo
        if (!caster.spendMp(mpCost)) {
            return; // No tiene MP
        }

        // Una sola línea reemplaza a todo el switch.
        caster.executeSkill(this, allyTarget, enemyTarget);
        
        endPlayerAction();
    }

    private void endPlayerAction() {
        if (isCombatOver()) {
            playerTurn = false;
            return;
        }
        currentCombatantIndex++;
        startNextTurn();
    }

    private void runSingleEnemyTurn(Enemy enemy) {
        enemy.executeTurn(this, party);
    }

    public List<Combatant> getTurnOrder() {
        return turnOrder;
    }

    public int getCurrentCombatantIndex() {
        return currentCombatantIndex;
    }
}
