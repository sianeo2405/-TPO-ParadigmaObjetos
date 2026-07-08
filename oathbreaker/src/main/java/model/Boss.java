package model;

public class Boss extends Enemy {
    private final int secondarySpeed;

    public Boss(String name, int maxHp, int attack, int defense, int speed, int goldReward, int xpReward) {
        super(name, maxHp, attack, defense, speed, goldReward, xpReward);
        this.secondarySpeed = 13;
    }

    public int getSecondarySpeed() {
        return secondarySpeed;
    }
}
