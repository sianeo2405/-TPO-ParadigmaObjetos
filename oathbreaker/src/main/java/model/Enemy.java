package model;

// Representa un enemigo en el juego, con estadísticas de combate y recompensas por derrotarlo.

public class Enemy extends Combatant {
    private int attackDebuff;
    private final int goldReward;
    private final int xpReward;

    public Enemy(String name, int maxHp, int attack, int defense, int speed, int goldReward, int xpReward) {
        super(name, maxHp, attack, defense, speed);
        this.goldReward = goldReward;
        this.xpReward = xpReward;
    }

    public void executeTurn(controller.CombatEngine engine, Party party) {
        java.util.List<PartyMember> targets = party.getAliveMembers();
        if (targets.isEmpty()) {
            return;
        }
        PartyMember target = targets.get(new java.util.Random().nextInt(targets.size()));
        int damage = getAttack() + new java.util.Random().nextInt(4);
        target.takeDamage(damage);
    }

    public int getGoldReward() {
        return goldReward;
    }

    public int getXpReward() {
        return xpReward;
    }

    @Override
    public int getAttack() {
        return Math.max(1, attack - attackDebuff);
    }

    @Override
    public boolean isPlayerControlled() {
        return false;
    }

    public void reduceAttack(int amount) {
        attackDebuff += amount;
    }

    public static Enemy goblin() {
        return new Enemy("Goblin", 60, 15, 2, 14, 15, 20);
    }

    public static Enemy wolf() {
        return new Wolf("Lobo", 55, 10, 3, 16, 20, 25);
    }

    public static Enemy skeleton() {
        return new Skeleton("Esqueleto", 45, 12, 5, 9, 25, 30);
    }

    public static Enemy orc() {
        return new Enemy("Orco", 100, 25, 6, 11, 30, 35);
    }

    public static Enemy darkMage() {
        return new DarkMage("Mago Oscuro", 80, 22, 4, 13, 25, 30);
    }

    public static Enemy boss() {
        return new Boss("Señor Dracónico", 750, 50, 10, 20, 0, 0);
    }

    @Override
    public String toString() {
        return name + " (" + currentHp + "/" + maxHp + " HP)";
    }
}
