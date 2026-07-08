package model;

import controller.CombatEngine;
import java.util.List;
import java.util.Random;

public class DarkMage extends Enemy {
    public DarkMage(String name, int maxHp, int attack, int defense, int speed, int goldReward, int xpReward) {
        super(name, maxHp, attack, defense, speed, goldReward, xpReward);
    }

    @Override
    public void executeTurn(CombatEngine engine, Party party) {
        List<PartyMember> targets = party.getAliveMembers();
        if (targets.isEmpty()) {
            return;
        }

        Random rand = new Random();
        PartyMember target = targets.get(rand.nextInt(targets.size()));
        int damage = getAttack() + rand.nextInt(4);
        target.takeDamage(damage);

        // Inflict flat Poison: 3 turns, 10 damage per turn
        target.applyPoison(3, 10);
    }
}
