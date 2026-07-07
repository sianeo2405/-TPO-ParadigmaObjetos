package model;

// Representa un objeto que puede ser utilizado por los miembros del grupo del jugador.
// Utiliza el Patrón Strategy (ItemEffect) para resolver su comportamiento polimórficamente sin usar switch.

public final class Item implements java.io.Serializable {
    
    // 1. LA INTERFAZ (La Estrategia)
    public interface ItemEffect extends java.io.Serializable {
        String apply(PartyMember target, int potency);
    }

    private final String name;
    private final String description;
    private final ItemEffect effect; // Reemplazamos el enum por la interfaz
    private final int cost;
    private final int potency;

    public Item(String name, String description, ItemEffect effect, int cost, int potency) {
        this.name = name;
        this.description = description;
        this.effect = effect;
        this.cost = cost;
        this.potency = potency;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public ItemEffect getEffect() { return effect; }
    public int getCost() { return cost; }
    public int getPotency() { return potency; }

    // 2. EJECUCIÓN POLIMÓRFICA (¡Chau switch!)
    public String applyTo(PartyMember target) {
        // El ítem no sabe qué hace el efecto, solo le dice "aplegate" y el efecto hace lo suyo.
        return effect.apply(target, potency); 
    }

    // 3. MÉTODOS DE FABRICACIÓN (Inyectamos las estrategias concretas)
    public static Item healthPotion() {
        return new Item("Poción de Salud", "Restaura HP a un miembro del grupo.", new HealEffect(), 50, 50);
    }

    public static Item manaPotion() {
        return new Item("Poción de Maná", "Restaura MP a un miembro del grupo.", new RestoreMpEffect(), 50, 30);
    }

    public static Item strengthElixir() {
        return new Item("Elixir de Fuerza", "Aumenta permanentemente el ataque.", new BuffAttackEffect(), 100, 5);
    }

    public static Item defenseElixir() {
        return new Item("Elixir de Defensa", "Aumenta permanentemente la defensa.", new BuffDefenseEffect(), 100, 5);
    }

    public static Item phoenixDown() {
        return new Item("Pluma de Fénix", "Revive a un miembro del grupo caído.", new ReviveEffect(), 200, 30);
    }

    public static Item megaPotion() {
        return new Item("Mega Poción", "Restaura la HP completamente.", new HealEffect(), 150, 10000);
    }

    @Override
    public String toString() {
        return name + ": " + description + " (Costo: " + cost + " monedas)";
    }

    // --- 4. CLASES CONCRETAS (Implementaciones del comportamiento) ---
    
    public static class HealEffect implements ItemEffect {
        @Override
        public String apply(PartyMember target, int potency) {
            int healed = Math.min(potency, target.getMaxHp() - target.getCurrentHp());
            target.heal(healed);
            return target.getName() + " recupera " + healed + " HP.";
        }
    }

    public static class RestoreMpEffect implements ItemEffect {
        @Override
        public String apply(PartyMember target, int potency) {
            int restored = Math.min(potency, target.getMaxMp() - target.getCurrentMp());
            target.restoreMp(restored);
            return target.getName() + " recupera " + restored + " MP.";
        }
    }

    public static class BuffAttackEffect implements ItemEffect {
        @Override
        public String apply(PartyMember target, int potency) {
            target.boostAttack(potency);
            return target.getName() + " aumenta su ataque permanentemente en " + potency + ".";
        }
    }

    public static class BuffDefenseEffect implements ItemEffect {
        @Override
        public String apply(PartyMember target, int potency) {
            target.boostDefense(potency);
            return target.getName() + " aumenta su defensa permanentemente en " + potency + ".";
        }
    }

    public static class ReviveEffect implements ItemEffect {
        @Override
        public String apply(PartyMember target, int potency) {
            if (target.isAlive()) {
                return target.getName() + " ya está vivo.";
            } else {
                target.revive(potency);
                return target.getName() + " ha sido revivido con la mitad de HP.";
            }
        }
    }
}