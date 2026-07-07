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
    //private int turnIndex;

    public CombatEngine(Party party) {
        this.party = party;
    }

    public void startEncounter(List<Enemy> encounterEnemies) {
        enemies.clear();
        enemies.addAll(encounterEnemies);
        log.clear();
        //turnIndex = 0;
        //append("¡Aparecen enemigos!");
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
        turnOrder.addAll(getAliveEnemies());
        turnOrder.sort((c1, c2) -> Integer.compare(c2.getSpeed(), c1.getSpeed()));
        currentCombatantIndex = 0;

        StringBuilder sb = new StringBuilder("Orden de turnos: ");
        for (int i = 0; i < turnOrder.size(); i++) {
            sb.append(turnOrder.get(i).getName());
            if (i < turnOrder.size() - 1) {
                sb.append(" -> ");
            }
        }
        //append(sb.toString());
    }

    private void startNextTurn() {
        if (isCombatOver() || isPartyWiped()) {
            return;
        }

        while (currentCombatantIndex < turnOrder.size()) {
            Combatant next = turnOrder.get(currentCombatantIndex);
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

        //turnIndex++;
        //append("— Turno " + (turnIndex + 1) + " —");
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
        //int restoredMp = attacker.restoreMp(attacker.getMaxMp()/10);
        //append(attacker.getName() + " ataca a " + target.getName() + " por " + damage + " daño y recupera " + restoredMp + " de MP.");
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

        // ¡MAGIA DE POO! Una sola línea reemplaza a todo el switch gigante.
        // El motor le dice al héroe: "Ejecutá tu habilidad, no me importa cómo lo hagas".
        caster.executeSkill(this, allyTarget, enemyTarget);
        
        endPlayerAction();
    }

    private void endPlayerAction() {
        if (isCombatOver()) {
            //append("Los enemigos caen. ¡Victoria!");
            playerTurn = false;
            return;
        }
        currentCombatantIndex++;
        startNextTurn();
    }

    private void runSingleEnemyTurn(Combatant enemy) {
        List<PartyMember> targets = party.getAliveMembers();
        if (targets.isEmpty()) {
            //append("Tu equipo fue derrotado...");
            return;
        }

        PartyMember target = targets.get(random.nextInt(targets.size()));
        int damage = enemy.getAttack() + random.nextInt(4);
        target.takeDamage(damage);
        //append(enemy.getName() + " ataca a " + target.getName() + " por " + damage + " daño.");
    }

    public List<Combatant> getTurnOrder() {
        return turnOrder;
    }

    public int getCurrentCombatantIndex() {
        return currentCombatantIndex;
    }

    /*private void append(String message) {
        log.add(message);
        if (log.size() > 80) {
            log.remove(0);
        }
    }*/
}
