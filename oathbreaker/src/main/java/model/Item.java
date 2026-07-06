package model;

// Representa un objeto que puede ser utilizado por los miembros del grupo del jugador, con efectos específicos 
// y un costo de oro asociado.

public final class Item implements java.io.Serializable {
    public enum Effect {
        HEAL_HP,
        RESTORE_MP,
        BUFF_ATTACK,
        BUFF_DEFENSE,
        REVIVE
    }
    private final String name;
    private final String description;
    private final Effect effect;
    private final int cost;
    private final int potency;

    public Item(String name, String description, Effect effect, int cost, int potency) {
        this.name = name;
        this.description = description;
        this.effect = effect;
        this.cost = cost;
        this.potency = potency;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Effect getEffect() {
        return effect;
    }

    public int getCost() {
        return cost;
    }

    public int getPotency() {
        return potency;
    }

    public String applyTo(PartyMember target) {
        return switch (effect) {
            case HEAL_HP -> {
                int healed = Math.min(potency, target.getMaxHp() - target.getCurrentHp());
                target.heal(healed);
                yield target.getName() + " recupera " + healed + " HP.";
            }
            case RESTORE_MP -> {
                int restored = Math.min(potency, target.getMaxMp() - target.getCurrentMp());
                target.restoreMp(restored);
                yield target.getName() + " recupera " + restored + " MP.";
            }
            case BUFF_ATTACK -> {
                target.boostAttack(potency);
                yield target.getName() + " aumenta su ataque en " + potency + ".";
            }
            case BUFF_DEFENSE -> {
                target.boostDefense(potency);
                yield target.getName() + " aumenta su defensa en " + potency + ".";
            }
            case REVIVE -> {
                if (target.isAlive()) {
                    yield target.getName() + " ya está vivo.";
                } else {
                    target.revive(potency);
                    yield target.getName() + " ha sido revivido con la mitad de HP.";
                }
            }
        };
    }

    public static Item healthPotion() {
        return new Item("Poción de Salud", "Restaura HP a un miembro del grupo.", Effect.HEAL_HP, 50, 50);
    }

    public static Item manaPotion() {
        return new Item("Poción de Maná", "Restaura MP a un miembro del grupo.", Effect.RESTORE_MP, 50, 30);
    }

    public static Item strengthElixir() {
        return new Item("Elixir de Fuerza", "Aumenta el ataque de un miembro del grupo.", Effect.BUFF_ATTACK, 100, 5);
    }

    public static Item defenseElixir() {
        return new Item("Elixir de Defensa", "Aumenta la defensa de un miembro del grupo.", Effect.BUFF_DEFENSE, 100, 5);
    }

    public static Item phoenixDown() {
        return new Item("Pluma de Fénix", "Revive a un miembro del grupo caído con algo de HP.", Effect.REVIVE, 200, 30);
    }

    public static Item megaPotion() {
        return new Item("Mega Poción", "Restaura la HP completamente.", Effect.HEAL_HP, 150, 10000);
    }

    @Override
    public String toString() {
        return name + ": " + description + " (Costo: " + cost + " monedas)";
    }
}